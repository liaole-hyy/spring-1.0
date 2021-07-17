package com.gupaoedu.spring.mybatis.interceptor;

public class LogInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Exception {
        System.out.println("log执行前:"+System.nanoTime());
        Object result = invocation.process();
        System.out.println("log执行后:"+System.nanoTime());
        return result;
    }
}
