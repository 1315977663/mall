package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-08 16:44
 **/
public interface ICartService {
    ServerResponse add(Integer productId, Integer count);
}
