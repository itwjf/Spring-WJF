package com.wjf.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component
public class WjfBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        System.out.println("初始化前");
        /**
         * 程序员继承这个接口，实现这个方法
         * 在这个方法中干自己想干的事情
         * 可以针对多个bean进行处理，也可以针对某个bean处理
         */

        //针对一个bean处理
        if (beanName.equals("userService")){
            //对userService这一个bean进行处理
        }

        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        /**
         * 程序员继承这个接口，实现这个方法
         * 在这个方法中干自己想干的事情
         */

        System.out.println("初始化后");


        return null;
    }
}
