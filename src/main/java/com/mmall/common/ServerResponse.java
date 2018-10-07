package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;


import java.io.Serializable;

/**
 * @program: mmall
 * @description: 封装响应对象
 * @author: fbl
 * @create: 2018-10-07 15:24
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) // 为空的省略key
public class ServerResponse<T> implements Serializable {
    private int status;
    private String message;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    private ServerResponse(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    @JsonIgnore // 屏蔽这个key
    public boolean isSuccess() {
        return this.status == 0;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static ServerResponse createBySuccess() {
        return new ServerResponse(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String message) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(T data, String message){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String message) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int coed, String message){
        return new ServerResponse<T>(coed, message);
    }


}
