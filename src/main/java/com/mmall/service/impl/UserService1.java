//package com.mmall.service.impl;
//
//import com.mmall.common.ServerResponse;
//import com.mmall.dao.UserMapper;
//import com.mmall.pojo.User;
//import com.mmall.service.IUserService;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * @program: mmall
// * @description:
// * @author: fbl
// * @create: 2018-10-07 16:11
// **/
////@Service("iUserService1") // @Autowire时 变量名和 iUserService1 一致
//public class UserService1 implements IUserService {
//
//    @Autowired
//    UserMapper userMapper;
//
//    @Override
//    public ServerResponse login(String username, String password) {
//        if(userMapper.checkUserName(username) == 0) {
//            return ServerResponse.createByErrorMessage("用户名bu存在");
//        }
//
//        User user = userMapper.selectLogin(username, password);
//
//        if(user == null) {
//            return ServerResponse.createByErrorMessage("密码00错误");
//        }
////        user.setPassword(StringUtils.EMPTY);
//        return ServerResponse.createBySuccess(user);
//    }
//}
