package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Cart;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-08 16:44
 **/
@Service("iCartService")
public class CartService implements ICartService {

    @Autowired
    CartMapper cartMapper;

    @Override
    public ServerResponse add(Integer productId, Integer count){

        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setQuantity(count);

        return null;
    }

}
