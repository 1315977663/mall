package com.mmall.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-24 17:24
 **/
@Component
@Aspect
public class RoleAuthAop {

    Logger logger = LoggerFactory.getLogger(RoleAuthAop.class);

    @Pointcut("execution(public * com.mmall.controller.backend.*.*(..))") // 横切有 Auth 注解的, controller包下的所有方法
    public void pointcut1() {}

    @Around("pointcut1()")
    public Object checkAuth(ProceedingJoinPoint point){
        logger.info("dasdsad");
        Object obj = null;
        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }
}
