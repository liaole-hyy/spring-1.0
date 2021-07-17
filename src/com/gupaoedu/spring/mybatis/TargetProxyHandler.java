package com.gupaoedu.spring.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TargetProxyHandler implements InvocationHandler {

    private Object target ;

    public TargetProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行前:"+System.nanoTime());
        Object result = method.invoke(target,args);
        System.out.println("执行后:"+System.nanoTime());
        return result;
    }
}
