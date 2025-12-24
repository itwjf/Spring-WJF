package com.spring;

/**
 * 表示bean中有的属性
 * 比如说当前bean的类型、作用域
 */
public class BeanDefinition {

    private Class clazz; //表示当前bean的类型
    private String scope; //表示当前bean的作用范围


    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
