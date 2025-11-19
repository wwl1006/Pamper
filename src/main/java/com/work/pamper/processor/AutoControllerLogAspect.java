package com.work.pamper.processor;

public class AutoControllerLogAspect {


}
//package com.work.pamper.processor;
//
//import com.work.pamper.annotation.AutoControlLog;
//import com.work.pamper.entity.ActionLog;
//import com.work.pamper.mapper.ActionLogMapper;
//import com.work.pamper.utils.JwtUtils;
//import com.work.pamper.utils.TimeUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//
//import jakarta.servlet.http.HttpServletRequest;
//
///**
// * 记录加了 @AutoControlLog 的控制器内每个请求的 action 名称与模块。
// */
//@Aspect
//@Component
//public class AutoControllerLogAspect {
//
//    @Autowired
//    private ActionLogMapper actionLogMapper;
//
//    // 拦截所有位于 controller 包下，并且类上标注了 @AutoControlLog 的方法
//    @Pointcut("@within(com.work.pamper.annotation.AutoControlLog) && execution(* com.work.pamper.controller..*(..))")
//    public void controllerWithAutoLog() {}
//
//    @AfterReturning(pointcut = "controllerWithAutoLog()")
//    public void afterController(JoinPoint joinPoint) {
//        try {
//            Class<?> targetClass = joinPoint.getTarget().getClass();
//            AutoControlLog classAnno = targetClass.getAnnotation(AutoControlLog.class);
//            String model = classAnno != null ? classAnno.model() : "";
//
//            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
//            RequestMapping rm = ms.getMethod().getAnnotation(RequestMapping.class);
//            String actionName = rm != null ? rm.name() : ms.getMethod().getName();
//
//            Long accountId = extractAccountIdFromJwt();
//
//            ActionLog log = new ActionLog();
//            log.setAccount_id(accountId);
//            log.setModel(model);
//            log.setAction(actionName);
//            log.setCreate_time(TimeUtils.getCurrentTimeString());
//
//            // 持久化日志，避免影响业务，异常吞掉即可
//            try {
//                actionLogMapper.insert(log);
//            } catch (Exception ignored) {
//                // 例如表未创建或映射缺失时，不影响接口返回
//            }
//        } catch (Exception ignored) {
//            // 任意异常不影响正常业务响应
//        }
//    }
//
//    private Long extractAccountIdFromJwt() {
//        try {
//            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
//            if (attrs == null) return null;
//            HttpServletRequest request = (HttpServletRequest) attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST);
//            if (request == null) return null;
//            String auth = request.getHeader("Authorization");
//            if (auth == null || auth.isEmpty()) return null;
//            String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
//            String subject = JwtUtils.getSubject(token);
//            return subject != null ? Long.valueOf(subject) : null;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}