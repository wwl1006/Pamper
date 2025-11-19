package com.work.pamper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 支持加在类和方法上（类上用于模块日志，方法上可做扩展）
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoControlLog {
    // 方法级别描述（可选）
    String value() default "";
    // 模块名（用于类级别）
    String model() default "";
}
