package com.mmall.security;

import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

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

    @Pointcut("execution(public * com.mmall.controller..*.*(..))" +
            "&& @annotation(auth)") // 横切有 Auth 注解的, controller包下的所有方法
    public void pointcut1(Auth auth) {}

    @Around("pointcut1(auth)")
    public Object checkAuth(ProceedingJoinPoint point, Auth auth){

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() > auth.role().getRoleCode()) { //Code 越小权限越大
            return  ServerResponse.createByErrorMessage("没有权限");
        }
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return ServerResponse.createByErrorMessage("系统错误");
        }
    }
}
