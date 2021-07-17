package com.gupaoedu.spring.demo.service.impl;

import com.gupaoedu.spring.annotation.Service;
import com.gupaoedu.spring.demo.service.IDemoSerivce;

import java.io.UnsupportedEncodingException;

@Service
public class DemoService implements IDemoSerivce {
    @Override
    public String get(String name) {
        //String str =new String(name.getBytes("UTF-8"),"UTF-8");
        return "<html><h3>I Love " +name+"</h3></html>";
    }
}
