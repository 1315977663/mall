package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-08 16:44
 **/
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId, String products);

    ServerResponse<CartVo> changeCheck(Integer userId, Integer productId, int checkStatus);

    ServerResponse<Integer> getCount(Integer userId);

    ServerResponse<CartVo> list(Integer userId);
}
