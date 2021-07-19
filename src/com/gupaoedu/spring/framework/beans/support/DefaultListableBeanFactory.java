package com.gupaoedu.spring.framework.beans.support;

import com.gupaoedu.spring.framework.beans.config.BeanDefinition;
import com.gupaoedu.spring.framework.core.BeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  默认的bean工厂
 */
public class DefaultListableBeanFactory implements BeanFactory {

    //保存bean的定义
    public Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String,BeanDefinition>();

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    public void doRegistBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for(BeanDefinition beanDefinition : beanDefinitions){
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw  new Exception("The " + beanDefinition.getFactoryBeanName() + "is exists !!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }
}
