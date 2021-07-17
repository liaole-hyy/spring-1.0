package com.gupaoedu.spring.mybatis.interceptor;

public interface Interceptor1 {
    Object intercept(Invocation invocation) throws Exception;

    Object plugin(Object target);
}
