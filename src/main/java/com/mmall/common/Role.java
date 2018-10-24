package com.mmall.common;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-24 17:19
 **/
public enum Role {
    ADMIN(0, "admin"), // 管理员
    CUSTOMER(1, "customer"); // 普通用户
    int roleCode;
    String roleName;
    Role(int roleCode, String roleName){
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    public int getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(int roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
