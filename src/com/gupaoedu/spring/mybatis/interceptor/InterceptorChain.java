package com.gupaoedu.spring.mybatis.interceptor;

import java.util.ArrayList;
import java.util.List;

public class InterceptorChain {

    private List<Interceptor1> interceptors =new ArrayList<>();

    public void addInterceptor(Interceptor1 interceptor1){
        this.interceptors.add(interceptor1);
    }

    public Object pluginAll(Object target){
        for (Interceptor1 interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }
}
