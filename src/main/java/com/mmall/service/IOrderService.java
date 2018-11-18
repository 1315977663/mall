package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
}
