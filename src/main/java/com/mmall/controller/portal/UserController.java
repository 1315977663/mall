package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-07 16:05
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {

        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if(!serverResponse.isSuccess()) {
            return serverResponse;
        }
        session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.GET)
    public ServerResponse<User> getUserInfo(HttpSession session) {
        return iUserService.getUserInfo(session);
    }
}
