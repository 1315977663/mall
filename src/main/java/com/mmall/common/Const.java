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

    public enum OrderStatus{
        CANCELLED(0, "已取消"),
        NOT_PAYING(10, "未付款"),
        PAYED(20, "已付款"),
        SHIPPED(40, "已发货"),
        SUCCESS(50, "交易成功"),
        CLOSE(60,"交易关闭")
        ;
        OrderStatus(int code, String msg) {
            this.code = code;
            this.msg =msg;
        }
        private int code;
        private String msg;

        public static String getMsg(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) {
                    return orderStatus.getMsg();
                }
            }
            return "无对应枚举";
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public enum PayPlatform{
        ALIPAY(1, "支付宝"),
        WECHAT(2, "微信");
        private int code;
        private String name;
        PayPlatform(int code, String name){
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum PaymentType{
        ONLINE(1, "在线支付");
        private int code;
        private String name;
        PaymentType(int code, String name){
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static String getName(int code) {

            for (PaymentType paymentType : values()) {
                if (paymentType.getCode() == code) {
                    return paymentType.getName();
                }
            }

            return "无对应枚举";

        }
    }
}
