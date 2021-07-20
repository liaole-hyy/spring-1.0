package com.gupaoedu.spring.framework.webmvc.servlet;

import java.io.File;
import java.util.logging.FileHandler;

public class ViewResolver {

    // .vm .ftl .jsp .gp .tom
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html" ;

    private File templateRootDir;

    public ViewResolver(String templateRoot) {
       String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
       templateRootDir = new File(templateRoot);
    }

    public View resolveViewName(String viewName){
        if( null == viewName || " ".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);

        File templateFile = new File((templateRootDir.getPath() + "/" +viewName)
                    .replaceAll("/+","/"));
        return new View(templateFile);
    }
}
