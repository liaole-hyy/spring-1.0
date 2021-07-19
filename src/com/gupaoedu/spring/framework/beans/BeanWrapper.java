package com.gupaoedu.spring.framework.beans;

public class BeanWrapper {

    private Object wrapperedInstance;

    private Class<?> wrappedClass;

    public BeanWrapper(Object instance) {
        this.wrapperedInstance = instance;
        this.wrappedClass = instance.getClass();
    }

    public Object getWrapperedInstance() {
        return this.wrapperedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedClass;
    }
}
