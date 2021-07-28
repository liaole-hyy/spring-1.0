package com.gupaoedu.spring.framework.aop.aspect;

import com.gupaoedu.spring.framework.aop.intercept.MethodInterceptor;
import com.gupaoedu.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice implements MethodInterceptor {

    private String throwName;

    public AspectJAfterThrowingAdvice(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable ex){
            invokeAdviceMethod(mi,null,ex);
            throw  ex;
        }
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
