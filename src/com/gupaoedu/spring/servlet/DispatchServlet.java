package com.gupaoedu.spring.servlet;

import com.gupaoedu.spring.annotation.*;
import com.gupaoedu.spring.demo.action.DemoAction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {

    private Properties contextConfig =new Properties();

    //IOC容器
    private Map<String,Object> beanMap = new ConcurrentHashMap<String,Object>();
    //所有初始化的bean的bean的类全名
    private List<String> classNames = new ArrayList<>();
    //url <------------> handler <------------>method
    private Map<String, Method> urlHandlerMap = new ConcurrentHashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-------------调用doPost--------------");
        //根据url委派给具体的调用方法
        try {
            doDispatch(req,resp);
        }catch (Exception e){
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail： "+ Arrays.toString(e.getStackTrace()));
        }

    }

    private void doDispatch(HttpServletRequest req,HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String url = req.getRequestURI();
        System.out.println("请求-------------"+url);
        String contextPath = req.getContextPath();
        System.out.println("contextPath---------------"+contextPath);
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        if(!this.urlHandlerMap.containsKey(url)){
            resp.getWriter().write("404 not found");
            return;
        }

        Map<String,String[]> params = req.getParameterMap();

        Method method = this.urlHandlerMap.get(url);

        //1、先把形参的位置和参数名字建立映射关系，并且缓存下来
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();

        Annotation[][] pa = method.getParameterAnnotations();
        for(int i = 0 ; i < pa.length ; i++){
            for(Annotation a : pa[i]){
                if(a instanceof RequestParam){
                    String paramName = ((RequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }

        Class<?>[] paramTypes = method.getParameterTypes();
        for(int i=0 ;i < paramTypes.length; i++){
            Class<?> type =paramTypes[i];
            if(type == HttpServletRequest.class || type== HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }

        //2. 根据参数位置匹配参数名字，参数名字获取url中对应的值
        Object[] paramValues = new Object[paramTypes.length];

        for(Map.Entry<String,String[]> param : params.entrySet()){
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if(!paramIndexMapping.containsKey(param.getKey())){
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());

            //涉及到类型强制转换
            paramValues[index] = value;
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index =  paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index =  paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        //method.invoke(beanMap.get(beanName),new Object[]{req,resp,params.get("name")[0]});
        method.invoke(beanMap.get(beanName),paramValues);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //开始初始化的进程
        System.out.println("----------------------spring init ");
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载
        doScanner(contextConfig.getProperty("scanPackage"));
        //注册
        doRegistry();
        //自动依赖注入
        //在spring中是通过调用getBean方法出发依赖注入的
        doAutowired();

//        DemoAction demoAction = (DemoAction) beanMap.get("demoAction");
//        demoAction.query(null,null,"liaole");

        //如果是SpringMvc会多设计一个hMapping;

        //将@RequestMapping中配置的url和一个method关联上
        //以便于从浏览器获得用户输入的url以后，能够找到具体执行的method
        initHandlerMapping();

    }

    private void initHandlerMapping(){
        //1.初始化<----------------->
        if(beanMap.isEmpty()){return;}

        for(Map.Entry<String,Object> entry : beanMap.entrySet()){
            Class clazz = entry.getValue().getClass();
            if(clazz.isAnnotationPresent(Controller.class)){
               //表示是控制层
                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    String rootPathUrl =  requestMapping.value();
                    //ok
                    System.out.println("controller--------------rootpath:"+rootPathUrl);

                    for(Method method : clazz.getMethods()){
                        if(!method.isAnnotationPresent(RequestMapping.class)){
                            continue;
                        }
                        RequestMapping requestMapping1 = method.getAnnotation(RequestMapping.class);
                        String url =("/"+rootPathUrl+"/"+requestMapping1.value()).replaceAll("/+","/");
                        urlHandlerMap.put(url,method);
                        System.out.println("Mapper: " + url +"--------->" +method);
                    }
                }
            }
        }

    }

    private void doAutowired(){
        if(beanMap.isEmpty()){return;}

        for(Map.Entry<String,Object> entry : beanMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for(Field field :fields){
                if(!field.isAnnotationPresent(Autowired.class)){ continue;}
                Autowired autowired =field.getAnnotation(Autowired.class);

                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();  //按类型去注入  获取类型名称
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegistry(){
        if(classNames.isEmpty()){ return;}

        try {
            for(String className :classNames){
                Class<?> clazz = Class.forName(className);

                //在Spring 中用的多个子方法来处理
                //
                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName =lowerFirstCase(clazz.getSimpleName());
                    //在Spring中这个阶段是不会直接put instance,这里put的是BeanDefinition;
                    beanMap.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){

                    Service service = clazz.getAnnotation(Service.class);
                    //默认用类名首字母注入，如果自己定义了beanName,那么优选用自己定义beanName
                    //如果是一个接口，使用接口的类型去自动注入
                    //在spring中同样会分别调用不同的方法autowireByName,autowriteByType

                    String beanName =service.value();
                    if("".equals(beanName.trim())){
                        beanName =lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance =clazz.newInstance();
                    beanMap.put(beanName,instance);

                    Class<?>[]  interfaces =clazz.getInterfaces();

                    for(Class<?> i:interfaces){
                        beanMap.put(i.getName(),instance);
                    }

                }else {
                    continue;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName){
        URL url =this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir =new File(url.getFile());

        for(File file :classDir.listFiles()){
                if(file.isDirectory()){
                    doScanner(packageName+"."+file.getName());
                }else {
                    if(!file.getName().endsWith(".class")){
                        continue;
                    }

                    classNames.add(packageName+"."+file.getName().replace(".class",""));
                }
        }
    }

    private void doLoadConfig(String location){
        //在spring中是通过Reader去查找和定位的
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private  String lowerFirstCase(String str){
        char[] chars =str.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
