package com.gupaoedu.spring.demo.action;

import com.gupaoedu.spring.annotation.Autowired;
import com.gupaoedu.spring.annotation.Controller;
import com.gupaoedu.spring.annotation.RequestMapping;
import com.gupaoedu.spring.annotation.RequestParam;
import com.gupaoedu.spring.demo.service.IDemoSerivce;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/query.json")
public class DemoAction {

    @Autowired
    private IDemoSerivce demoSerivce;

    @RequestMapping("/index.html")
    public void query(HttpServletRequest req, HttpServletResponse resp,@RequestParam(value = "name") String name) throws UnsupportedEncodingException {
        String result = demoSerivce.get(name);
        System.out.println(result);
        try {
            resp.getWriter().write(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
