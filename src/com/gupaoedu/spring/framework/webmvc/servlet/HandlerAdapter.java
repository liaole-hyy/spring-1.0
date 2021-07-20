package com.gupaoedu.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerAdapter {

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) {
        return new ModelAndView("xx");
    }
}
