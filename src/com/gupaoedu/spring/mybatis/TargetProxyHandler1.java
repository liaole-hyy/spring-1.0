package com.gupaoedu.spring.mybatis;

import com.gupaoedu.spring.mybatis.interceptor.Interceptor1;
import com.gupaoedu.spring.mybatis.interceptor.Invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TargetProxyHandler1 implements InvocationHandler {

    private Object target ;

    private Interceptor1 interceptor;

    public TargetProxyHandler1(Object target, Interceptor1 interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation(target,method,args);
        return interceptor.intercept(invocation);
    }
}
