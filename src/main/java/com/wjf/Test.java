package com.wjf;

import com.spring.WjfApplicationContext;

public class Test {

    public static void main(String[] args) {

        WjfApplicationContext applicationContext = new WjfApplicationContext(AppConfig.class);

        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));


    }
}
