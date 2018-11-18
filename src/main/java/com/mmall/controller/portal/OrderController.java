package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order")
@Auth(role = Role.CUSTOMER)
public class OrderController {

    @Autowired
    HttpSession session;

    @Autowired
    IOrderService iOrderService;

    @RequestMapping("/pay.do")
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        String path = request.getSession().getServletContext().getRealPath("upload"); // 获取运行upload路径
        return iOrderService.pay(currentUser.getId(), orderNo, path);
    }
}
