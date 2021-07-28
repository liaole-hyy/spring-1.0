package com.gupaoedu.spring.framework.aop.aspect;

import com.gupaoedu.spring.framework.aop.intercept.MethodInterceptor;
import com.gupaoedu.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodBeforeAdviceInterceptor extends AbstractAspectJAdvice implements MethodInterceptor {

    private JoinPoint jp ;
    public MethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    public void before(Method method, Object[] arguments, Object aThis) throws Throwable{
        invokeAdviceMethod(this.jp,null,null);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        jp = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
