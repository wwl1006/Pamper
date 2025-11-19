package com.work.pamper.processor;

import com.work.pamper.annotation.AutoControlLog;

import java.lang.reflect.Method;

public class AutoControllerLogProcessor {
    public static void processAnnotations(Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(AutoControlLog.class)) {
                AutoControlLog autoControlLog = method.getAnnotation(AutoControlLog.class);
                System.out.println("执行方法: " + method.getName() + "; 描述: " + autoControlLog.value());
            }
        }
    }
}
