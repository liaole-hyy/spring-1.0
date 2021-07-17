package com.gupaoedu.spring.mybatis.interceptor;

import com.gupaoedu.spring.mybatis.TargetProxyFactory1;

public class LogInterceptor1 implements Interceptor1 {

    @Override
    public Object intercept(Invocation invocation) throws Exception {
        System.out.println("log执行前:"+System.nanoTime());
        Object result = invocation.process();
        System.out.println("log执行后:"+System.nanoTime());
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return TargetProxyFactory1.newProxy(target,this);
    }
}
