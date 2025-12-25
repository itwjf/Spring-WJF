package com.wjf.service;


import com.spring.*;

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("自己在初始化方法决定做什么，spring只是提供这样一个机制;" +
                "在创建这个bean的时候，就会调用这个方法");
    }

    /**
     * Spring会把当前创建的Bean的名字传给这个set方法
     * 这个方法中想干什么就干什么
     * @param name 当前创建的bean的名字
     */
    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }
}
