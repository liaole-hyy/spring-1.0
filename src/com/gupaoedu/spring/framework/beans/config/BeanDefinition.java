package com.gupaoedu.spring.framework.beans.config;

/**
 *  bean的定义
 */
public class BeanDefinition {

    public boolean isLazyInit(){
        return  false;
    }
    private String factoryBeanName ; //beanName ;
    private String beanClassName ; //原生类的全类名

    public boolean isSingleton(){return true;}

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
