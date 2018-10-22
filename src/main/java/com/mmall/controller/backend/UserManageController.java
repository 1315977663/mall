package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    IUserService iUserService;

    @RequestMapping("/login")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, user);
            } else {
                return ServerResponse.createByErrorMessage("非管理员用户");
            }
        }
        return serverResponse;
    }

    @RequestMapping("/list")
    public ServerResponse<PageBean<User>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5")int pageSize) {
        return iUserService.getUserList(pageNum, pageSize);
    }

}
