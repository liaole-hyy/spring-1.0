package com.gupaoedu.spring.demo.action;

import com.gupaoedu.spring.annotation.Autowired;
import com.gupaoedu.spring.annotation.Controller;
import com.gupaoedu.spring.annotation.RequestMapping;
import com.gupaoedu.spring.demo.service.IDemoSerivce;

@Controller
public class MyAction {

    @Autowired
    IDemoSerivce demoSerivce;

    @RequestMapping("/index.html")
    public void query(){

    }
}
