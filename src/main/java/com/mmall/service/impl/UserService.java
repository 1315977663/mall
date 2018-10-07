package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
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
        // MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);

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

    @Override
    public ServerResponse register(User user) {
        if (userMapper.checkUserName(user.getUsername()) != 0 ) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        if (userMapper.checkEmail(user.getEmail()) != 0) {
            return ServerResponse.createByErrorMessage("邮箱已被占用");
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return  ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            switch (type) {
                case Const.EMAIL:
                    if (userMapper.checkEmail(str) != 0) {
                        return ServerResponse.createByErrorMessage("邮箱已被占用");
                    }
                    break;
                case Const.USER_NAME:
                    if (userMapper.checkUserName(str) != 0) {
                        return ServerResponse.createByErrorMessage("用户名已存在");
                    }
                    break;
                default:
                    return ServerResponse.createByErrorMessage("type参数错误");
            }
        } else {
            return ServerResponse.createByErrorMessage("type参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
}
