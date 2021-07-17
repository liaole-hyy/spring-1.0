package com.gupaoedu.spring.mybatis.interceptor;

import java.lang.reflect.Method;

public class Invocation {

    private Object target ;
    private Method method ;
    private Object[] args;

    public Invocation(Object target,Method method,Object[] args){
        this.target = target ;
        this.method = method ;
        this.args = args ;
    }

    public Object process() throws Exception {
        return method.invoke(target,args);
    }
}
