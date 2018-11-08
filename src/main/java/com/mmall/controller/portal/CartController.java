package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/car")
@Auth(role = Role.CUSTOMER)
public class CartController {

    @Autowired
    HttpSession session;

    @Autowired
    ICartService iCartService;

    @RequestMapping("/add")
    public ServerResponse<CartVo> add(Integer productId, Integer count){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.add(currentUser.getId(), productId, count);
    }
}
