package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-07 16:11
 **/
@Service("iUserService") // @Autowire时 变量名和 iUserService 一致
public class UserService implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        if(userMapper.checkUserName(username) == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        User user = userMapper.selectLogin(username, password);

        if(user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户还未登录，无法获取用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }
}
