package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

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
        int ROLE_CUSTOMER = 1; // 普通用用户
        int ROLE_ADMIN = 0; // 管理元用户
    }

    public interface ProductListOrderBy{
        Set<String> orderByList = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface ProductStatus{
        int SELLING = 1; // 售卖中
        int SELLING_END = 2; // 下架
        int DELETE = 3; // 删除
    }

    public interface CartStatus{
        int UN_CHECK = 0; // 未选中
        int CHECKED = 1; // 选中
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
    }

}
