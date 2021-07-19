package com.gupaoedu.spring.framework.core;

/**
 *  spring ioc 顶层类
 */
public interface BeanFactory {

    Object getBean(String beanName);

    Object getBean(Class beanClass);
}
