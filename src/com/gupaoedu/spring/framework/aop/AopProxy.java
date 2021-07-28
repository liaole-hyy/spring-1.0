package com.gupaoedu.spring.framework.aop;

/**
 *  aop代理顶层类
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
