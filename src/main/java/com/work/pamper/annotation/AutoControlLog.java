package com.work.pamper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 指定该注解可以用在方法上
@Target(ElementType.METHOD)
// 指定注解的保留策略
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoControlLog {
    String value() default "Default Value";
    int number() default 0;
}
