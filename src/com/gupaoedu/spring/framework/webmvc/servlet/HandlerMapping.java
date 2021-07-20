package com.gupaoedu.spring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class HandlerMapping {

    private Object controller ;

    protected Method method;

    protected Pattern pattern;


    public HandlerMapping(Pattern pattern, Object instance, Method method) {
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
