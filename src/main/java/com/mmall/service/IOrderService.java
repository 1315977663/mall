package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallback(Map<String, String[]> requestParams);

    ServerResponse<Boolean> queryOrderPayStatus(Long orderNo, Integer userId);
}
