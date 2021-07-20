package com.gupaoedu.spring.framework.webmvc.servlet;

import java.util.Map;

public class ModelAndView  {
    private String viewName ;
    private Map<String,?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
