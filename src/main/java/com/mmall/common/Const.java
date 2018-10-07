package com.mmall.common;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-07 16:39
 **/
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USER_NAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0; // 普通用用户
        int ROLE_ADMIN = 1; // 管理元用户
    }
}
