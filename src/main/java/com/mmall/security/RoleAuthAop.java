package com.mmall.security;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

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

    @Autowired
    HttpSession session;

    @Pointcut("execution(public * com.mmall.controller..*.*(..))") // 横切有controller包下的所有方法
    public void pointcut() {}

    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint point){

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        Auth auth = this.getAuth(point);

        if(auth != null){
            if(currentUser == null) {
                return ServerResponse.createByErrorMessage("没有登录");
            }
            if(currentUser.getRole() > auth.role().getRoleCode()) { //Code 越小权限越大
                return  ServerResponse.createByErrorMessage("没有权限");
            }
        }

        try {
            return point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return ServerResponse.createByErrorMessage("系统错误");
        }
    }


    //  优先获取方法上的注解，再获取类上的注解
    private Auth getAuth(ProceedingJoinPoint point){
        // 获取切入的方法
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        boolean flag = method.isAnnotationPresent(Auth.class); // 判断方法上是否有 AUTH 注解
        if(flag){
            return method.getAnnotation(Auth.class);
        }

        Auth auth = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Auth.class);
        if(auth != null){
            return auth;
        }

        return null;
    }
}
