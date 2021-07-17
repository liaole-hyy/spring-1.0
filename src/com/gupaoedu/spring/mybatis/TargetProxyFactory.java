package com.gupaoedu.spring.mybatis;

import java.lang.reflect.Proxy;

public class TargetProxyFactory {

    public static Object newProxy(Object target){
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new TargetProxyHandler(target));
    }

    public static void main(String[] args) {
        Executor target = new DefaultExecutor();
        Executor executor = (Executor) TargetProxyFactory.newProxy(target);
        executor.execute("select * from user");
    }
}
