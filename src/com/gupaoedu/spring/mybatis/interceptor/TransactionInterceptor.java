package com.gupaoedu.spring.mybatis.interceptor;

import com.gupaoedu.spring.mybatis.TargetProxyFactory1;

public class TransactionInterceptor  implements Interceptor1{
    @Override
    public Object intercept(Invocation invocation) throws Exception {
        System.out.println("事务提交前");
        Object result = invocation.process();
        System.out.println("事务提交后");
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return TargetProxyFactory1.newProxy(target,this);
    }
}
