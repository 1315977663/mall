package com.mmall.controller.portal;

import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.security.Auth;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car")
@Auth(role = Role.CUSTOMER)
public class CartController {

    @RequestMapping("/add")
    public ServerResponse add(Integer productId, Integer count){
        return null;
    }
}
