package com.gupaoedu.spring.mybatis;


import com.gupaoedu.spring.mybatis.interceptor.*;

import java.lang.reflect.Proxy;

public class TargetProxyFactory1 {

    public static Object newProxy(Object target, Interceptor1 interceptor){
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new TargetProxyHandler1(target,interceptor));
    }

    public static void main(String[] args) {
//        Executor target = new DefaultExecutor();
//
//        Interceptor1 logIntercetor = new LogInterceptor1();
//
//        Executor executor = (Executor) TargetProxyFactory1.newProxy(target,logIntercetor);
//
//        executor.execute("select * from user");


        Executor target = new DefaultExecutor();

        Interceptor1 logInterceptor1 = new LogInterceptor1();
        Interceptor1 transactionInterceptor = new TransactionInterceptor();

        InterceptorChain interceptorChain = new InterceptorChain();
        interceptorChain.addInterceptor(logInterceptor1);
        interceptorChain.addInterceptor(transactionInterceptor);

        Executor executor = (Executor) interceptorChain.pluginAll(target);
        executor.execute("select * from user");
    }
}
