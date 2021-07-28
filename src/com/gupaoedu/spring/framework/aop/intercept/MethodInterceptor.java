package com.gupaoedu.spring.framework.aop.intercept;

public interface MethodInterceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;
}
