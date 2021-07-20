package com.gupaoedu.spring.framework.webmvc.servlet;

import com.gupaoedu.spring.framework.annotation.Controller;
import com.gupaoedu.spring.framework.annotation.RequestMapping;
import com.gupaoedu.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  MVC Servlet
 */
public class DispatcherServlet extends HttpServlet {

    //保存controller中URL和Method的对应关系
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    private ApplicationContext applicationContext = null;

    /**
     *  初始化  进行ServletMVC 组件初始化
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws  ServletException {
        //spring 容器初始化
        applicationContext  = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
        // ============== MVC =============
        initStrategies(applicationContext);

        System.out.println("LiaoLe Spring  Framework is init");
    }

    /**
     *  mvc组件初始化
     * @param context
     */
    private void initStrategies(ApplicationContext context) {
        //handlerMapping
        initHandlerMappings(context);
        //初始化参数配置
        initHandlerAdapters(context);
        //初始化视图解析器
        initViewResolvers(context);
    }

    private void initViewResolvers(ApplicationContext context) {
        String templateRoot  = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRoot);
        for(File file : templateRootDir.listFiles()){
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for(HandlerMapping handlerMapping : handlerMappings){
            this.handlerAdapters.put(handlerMapping,new HandlerAdapter());
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        if( this.applicationContext.getBeanDefinitionCount() == 0 ){
            return;
        }

        for(String beanName : this.applicationContext.getBeanDefinitionNames()){
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();

            if(!clazz.isAnnotationPresent(Controller.class)){
                continue;
            }

            String baseUrl = "";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                //获取当前注解的value值
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //只迭代public方法
            for(Method method: clazz.getMethods()){
                if(!method.isAnnotationPresent(RequestMapping.class)){
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                //  //demo, //query
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("\\*",".*")
                        .replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);

                handlerMappings.add(new HandlerMapping(pattern,instance,method));
                System.out.println("Mapped : " + regex + " --> " + method);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //根据URL委派给具体的调用方法
        try {
            doDispatch(req,resp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 分发调度器
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、根据URL 拿到对应的Handler
        HandlerMapping handler = getHandler(req);

        if(null == handler){
            processDispatchResult(req,resp,new ModelAndView("404"));
        }
        //2、根据HandlerMapping拿到HandlerAdapter
        HandlerAdapter ha = getHandlerAdapter(handler);
        //3、根据HandlerAdapter拿到对应的ModelAndView
        ModelAndView mv = ha.handle(req,resp,handler);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){ return null;}
        HandlerAdapter ha = this.handlerAdapters.get(handler);
        return ha;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        if(null == mv ) {
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }

        for(ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(),req,resp);
            return;
        }

    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url =url.replaceAll(contextPath,"").replaceAll("/+","/");

        for(HandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if(!matcher.matches()){ continue;}
            return handlerMapping;
        }
        return null;
    }

}
