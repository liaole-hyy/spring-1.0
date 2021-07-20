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
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/query.json")
public class DemoAction {

    private static final ThreadLocal<Set<String>> ids = new ThreadLocal<Set<String>>(){
        @Override
        protected Set<String> initialValue() {
            return new HashSet<>();
        }
    };

    @Autowired
    private IDemoSerivce demoSerivce;

    @RequestMapping("/index.html")
    public void query(HttpServletRequest req, HttpServletResponse resp,@RequestParam(value = "name") String name) throws UnsupportedEncodingException {
        ids.get().add(name);
        String result = demoSerivce.get(name);
        ids.get().add("liaole"+name);
        try {
            Thread.sleep(1000*60);
            for(String str : ids.get()){
                System.out.println("threadlocal---------------"+str);
            }
            resp.getWriter().write(result);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
