package com.gupaoedu.spring.framework.aop.aspect;

import com.gupaoedu.spring.framework.aop.intercept.MethodInterceptor;
import com.gupaoedu.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AfterReturningAdviceInterceptor extends AbstractAspectJAdvice implements MethodInterceptor {

    private JoinPoint jp ;

    public AfterReturningAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    private void afterReturning(Object returnValue,Method method,Object[] args,Object target) throws Throwable{
        this.invokeAdviceMethod(this.jp,returnValue,null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.jp = mi ;
        Object retVal = mi.proceed();
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }
}
