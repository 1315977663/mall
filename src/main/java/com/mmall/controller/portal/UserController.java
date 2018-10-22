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

    // 用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {

        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if(!serverResponse.isSuccess()) {
            return serverResponse;
        }
        session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        return serverResponse;
    }

    // 获取登录用户信息
    @RequestMapping(value = "/get_user_info", method = RequestMethod.GET)
    public ServerResponse<User> getUserInfo(HttpSession session) {
        return iUserService.getUserInfo(session);
    }


    // 退出登录.
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ServerResponse register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "/check_valid", method = RequestMethod.GET)
    public ServerResponse checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "/forget_get_question", method = RequestMethod.GET)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.getQuestion(username);
    }

    @RequestMapping(value = "/forget_check_answer", method = RequestMethod.GET)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "/forget_reset_password", method = RequestMethod.GET)
    public ServerResponse userForgetResetPassword(String username, String passwordNew, String token) {
        return iUserService.userForgetResetPassword(username, passwordNew, token);
    }

    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ServerResponse userResetPassword(String password, String passwordNew, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户没有登录");
        }
        return iUserService.userResetPassword(currentUser.getUsername(), password, passwordNew);
    }

    @RequestMapping(value = "/update_information", method = RequestMethod.POST)
    public ServerResponse updateInformation(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户没有登录");
        }

        ServerResponse<User> serverResponse = iUserService.updateInformation(user, currentUser);

        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    @RequestMapping(value = "/get_information", method = RequestMethod.GET)
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户没有登录，请登录");
        }
        ServerResponse<User> serverResponse = iUserService.getInformation(currentUser);
        if(serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }
}
