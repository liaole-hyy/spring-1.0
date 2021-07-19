package com.gupaoedu.spring.framework.annotation;

import java.lang.annotation.*;

/**
 *  页面交互控制器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
