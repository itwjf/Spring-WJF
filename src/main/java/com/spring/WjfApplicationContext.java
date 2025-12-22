package com.spring;

import java.io.File;
import java.net.URL;
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

        // 1.解析ComponentScan注解; 拿到配置类上的注解
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value(); // 拿到这个注解上定义的属性的值，也就是包扫描路径
        path.replace(".","/"); // 把路径中的点，替换成斜杠

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
                        Class<?> clazz = classLoader.loadClass(className);
                        // 判断类上是否有Component注解
                        if (clazz.isAnnotationPresent(Component.class)){
                            // 表示当前这个类是一个bean
                            // 解析类----->BeanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value(); // 获取组件名称

                            BeanDefinition beanDefinition = new BeanDefinition();
                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else{
                                beanDefinition.setScope("singleton"); // 默认单例
                            }


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
        if(beanDefinitionMap.containsKey(beanName)){

        }else {
            // 不存在对应的bean
            throw new NullPointerException();
        }
        return null;
    }


}
