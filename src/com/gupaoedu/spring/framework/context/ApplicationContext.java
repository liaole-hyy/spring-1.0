package com.gupaoedu.spring.framework.context;

import com.gupaoedu.spring.framework.annotation.Autowired;
import com.gupaoedu.spring.framework.annotation.Controller;
import com.gupaoedu.spring.framework.annotation.Service;
import com.gupaoedu.spring.framework.beans.BeanWrapper;
import com.gupaoedu.spring.framework.beans.config.BeanDefinition;
import com.gupaoedu.spring.framework.beans.support.BeanDefinitionReader;
import com.gupaoedu.spring.framework.beans.support.DefaultListableBeanFactory;
import com.gupaoedu.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.*;

public class ApplicationContext implements BeanFactory {

    // 默认工厂
    private DefaultListableBeanFactory registry = new DefaultListableBeanFactory();

    //循环依赖的标识，当前正在创建的BeanName，Mark一下
    private Set<String> singletonsCurrentlyInCreation = new HashSet<String>();

    //一级缓存:保存成熟的bean
    private Map<String,Object> singletonObjects = new HashMap<String, Object>();

    //二级缓存：保存早期的bean
    private Map<String,Object> earlySingletonObjects = new HashMap<String, Object>();

    //三级缓存（终极缓存）
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new HashMap<String, BeanWrapper>();
    private Map<String,Object> factoryBeanObjectCache = new HashMap<String, Object>();

    private BeanDefinitionReader reader ;

    /**
     *  初始化
     * @param configLocations
     */
    public ApplicationContext(String... configLocations) {
        //1.加载配置文件
        reader = new BeanDefinitionReader(configLocations);
        try {
            //2.解析配置文件，将所有配置信息封装在beanDefinition对象
            List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            //3.将所有信息缓存起来
            this.registry.doRegistBeanDefinition(beanDefinitions);
            //4.加载所有非延迟的bean
            doLoadInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  加载所有非延迟的bean
     */
    private void doLoadInstance() {
        //循环调用getBean()方法;
        for(Map.Entry<String,BeanDefinition> entry : this.registry.beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            if(!entry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    /**
     * 从IOC 容器中获得一个Bean对象
     * @param beanName
     * @return 返回值
     */
    @Override
    public Object getBean(String beanName) {
        //1.先拿到BeanDefinition配置信息
        BeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);

        //2.先拿单例bean
        Object singleton = getSingleton(beanName,beanDefinition);
        if(singleton != null){ return  singleton; }

        //标记bean正在创建
        if(!singletonsCurrentlyInCreation.contains(beanName)){
            singletonsCurrentlyInCreation.add(beanName);
        }

        //3.反射实例化对象
        Object instance = instantiateBean(beanName,beanDefinition);

        //input to singletonObjects cache
        this.singletonObjects.put(beanName,instance);

        //4.将返回的Bean的对象封装Wrapper
        BeanWrapper beanWrapper = new BeanWrapper(instance);

        //5、执行依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);

        //6、保存到IoC容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        return beanWrapper.getWrapperedInstance();
    }

    /**
     * 依赖注入
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();

        if(!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }
        //忽略字段的修饰符，不管你是 private / protected / public / default
        for(Field field : clazz.getDeclaredFields()){
            if(!field.isAnnotationPresent(Autowired.class)){ continue; }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                field.set(instance,getBean(autowiredBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Object getSingleton(String beanName, BeanDefinition beanDefinition) {
        //先去一级缓存去拿
        Object bean = singletonObjects.get(beanName);
        //如果一级缓存没有，但是又有创建标识，说明是循环依赖
        if( null == bean && singletonsCurrentlyInCreation.contains(beanName)){
            bean = earlySingletonObjects.get(beanName);
            if(bean == null){
                //如果二级缓存也没有，那就从三级缓存中拿
                bean = instantiateBean(beanName,beanDefinition);
                earlySingletonObjects.put(beanName,bean);
            }

        }
        return bean;
    }

    /**
     * 终极缓存初始化
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        if(beanDefinition.isSingleton() && this.factoryBeanObjectCache.containsKey(beanName)){
            return this.factoryBeanObjectCache.get(beanName);
        }
        String className  = beanDefinition.getBeanClassName();
        Object instance = null ;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            this.factoryBeanObjectCache.put(beanName,instance);
            this.factoryBeanObjectCache.put(clazz.getName(),instance);
            for (Class<?> i : clazz.getInterfaces()) {
                this.factoryBeanObjectCache.put(i.getName(),instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    @Override
    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }
}
