package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

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

    // 校验用户名，邮箱不存在。不存在返回成功
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

    @Override
    public ServerResponse<String> getQuestion(String username) {
        ServerResponse checkResponse = this.checkValid(username, Const.USER_NAME);
        if (checkResponse.isSuccess()) {
            return  ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByName(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        if (userMapper.checkUserAnswer(username, question, answer) > 0) {
            String token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_KEY_PREFIX + username, token);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse userForgetResetPassword(String username, String passwordNew, String token) {

        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token是必须参数");
        }
        String tokenCache = TokenCache.getKey(TokenCache.TOKEN_KEY_PREFIX + username);
        if (StringUtils.equals(token, tokenCache)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int result = userMapper.updatePassword(username, md5Password);
            if (result > 0) {
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误");
        }

        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse userResetPassword(String username, String password, String passwordNew) {
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user != null) {
          int result = userMapper.updatePassword(username, MD5Util.MD5EncodeUtf8(passwordNew));
          if (result > 0) {
              return ServerResponse.createBySuccessMessage("密码修改成功");
          }
        } else {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        return ServerResponse.createByErrorMessage("修改失败，请重试");
    }
}
