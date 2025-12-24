package com.wjf;

import com.spring.WjfApplicationContext;
import com.wjf.service.UserService;

public class Test {

    public static void main(String[] args) {

        WjfApplicationContext applicationContext = new WjfApplicationContext(AppConfig.class);

        UserService userService =(UserService) applicationContext.getBean("userService");

        userService.test();

    }
}
