package com.mmall.security;

import com.mmall.common.Role;

import java.lang.annotation.*;

/**
 * 该注解进行权限认证，登录检测
 * 可以运用在类和方法上
 */

// 该注解可以运用在class和方法上
@Target({ElementType.TYPE, ElementType.METHOD})
// 表示运行时有效，可以通过反射获取去到
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    Role role() default Role.ADMIN;
}
