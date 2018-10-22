package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    int checkEmail(String email);

    User selectLogin(@Param("username") String userName, @Param("password") String password);

    String selectQuestionByName(String username);

    int checkUserAnswer(@Param("username")String username, @Param("question") String question, @Param("answer") String answer);

    int updatePassword(@Param("username") String username, @Param("password") String password);

    int checkEmailById(@Param("email") String email, @Param("userId") int userId);

    List<User> getUserList();
}