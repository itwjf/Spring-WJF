package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//模拟Spring的ApplicationContext
public class WjfApplicationContext {

    // 当前用户需要传给Spring容器的一个配置类
    private Class configClass;

    // 用来存放单例bean的容器
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();

    // 用来存放BeanDefinition的容器
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public WjfApplicationContext(Class configClass) {
        this.configClass = configClass;

        //解析配置类

        //ComponentScan注解--->扫描路径-->扫描-->BeanDefinition--->BeanDefinitionMap
        sacn(configClass); //扫描配置文件，得到所有的beanDefinition

        /**
         * 根据所有的beanDefinition创建bean
         * Entry<key,value>:
         */
        for (Map.Entry<String,BeanDefinition> entry : beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,bean);
            }
        }
    }

    /**
     *
     * @param beanName bean的名字
     * @param beanDefinition 根据beanDefinition创建bean
     * @return 创建的实列
     */
    public Object createBean(String beanName,BeanDefinition beanDefinition){

        //根据beanDefinition得到bean的类型，得到Class对象
        Class clazz = beanDefinition.getClazz();
        try {
            //根据class对象，实例化bean;
            Object instance = clazz.getDeclaredConstructor().newInstance();


            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) { //通过class对象知道有哪些属性
                //判断属性是否加了Autowired注解，才表示需要注入
                if(declaredField.isAnnotationPresent(Autowired.class)){

                    //getBean方法，会根据属性的名字去容器中获取bean
                    Object bean = getBean(declaredField.getName());
                    //这里可以加一个抛出异常判断：如果没有根据这个信息在容器中找到bean，抛出异常
//                    if(bean == null){
//
//                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }

            //Aware回调
            /** 判断当前实列有没有实现BeanNameAware接口，如果实现了则将BeanName传给这个实列 */
            if(instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //初始化
            if(instance instanceof InitializingBean){
                try {
                    ((InitializingBean) instance).afterPropertiesSet();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void sacn(Class configClass) {
        // 1.解析ComponentScan注解; 拿到配置类上的注解
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value(); // 拿到这个注解上定义的属性的值，也就是包扫描路径
        path = path.replace(".","/"); // 把路径中的点，替换成斜杠

        // 2.扫描路径下的类，进行实例化（反射）
        ClassLoader classLoader = WjfApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path); // 通过类加载器，获取这个路径下的资源
        File file = new File(resource.getFile());


        if (file.isDirectory()){ // 如果是一个文件夹

            File[] files = file.listFiles(); // 获取这个文件夹下的所有文件

            // 遍历这些文件
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                if(fileName.endsWith(".class")){ // 如果是一个class文件

                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", "."); // 把路径中的斜杠，替换成点
                    try {
                        Class<?> clazz = classLoader.loadClass(className); //根据名字拿到class对象
                        if (clazz.isAnnotationPresent(Component.class)){ // 判断类上是否有Component注解，表示当前这个类是一个bean
                            // 解析类----->BeanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value(); // 获取组件名称

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            //获取scope注解定义bean的作用范围
                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                //给bean的定义添加bean的scope属性
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else{
                                beanDefinition.setScope("singleton"); // 默认单例
                            }

                            /**
                             * 将bean的定义（beanDefinition）添加在容器中
                             * 这样就可以通过bean的名字知道bean的定义（key，value）
                             */
                            beanDefinitionMap.put(beanName,beanDefinition);


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public Object getBean(String beanName) {
        if(beanDefinitionMap.containsKey(beanName)){ //判断当前名字的bean是否存在
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName); //拿到这个bean的beanDefinition对象
            if(beanDefinition.getScope().equals("singleton")){  //根据bean的定义判断这个bean是不是单例的
                Object o = singletonObjects.get(beanName); //如果是单例的，则从单例池中拿这个对象
                return o;
            }else {
                // 创建bean对象
                Object bean = createBean(beanName,beanDefinition);
                return bean;
            }
        }else {
            // 不存在对应的bean
            throw new NullPointerException();
        }

    }


}
