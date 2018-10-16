package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<User> getUserInfo(HttpSession session);
    ServerResponse register(User user);
    ServerResponse checkValid(String str, String type);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse userForgetResetPassword(String username, String passwordNew, String token);
}
