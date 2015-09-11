package com.foxxy.git.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MethodLimiter {
    
    /**
     * 速率控制器唯一标识
     */
    String name();

    String beanName() default "rejectTaskLimitStrategy";
    
    /**
     * 控制速率
     */
    int rate() default 0;

    int millSeconds() default 0;

}
