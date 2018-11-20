package com.mmall.dao;

import com.alipay.api.domain.Car;
import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int allNnChecked(Integer userId);

    int deleteByProducts(@Param("userId") Integer userId, @Param("productIds") List<String> productList);

    int changeCheck(@Param("userId") Integer userId, @Param("productId") Integer productId , @Param("checkStatus") Integer checkStatus);

    int getCountByUserId(Integer userId);

    List<Cart> selectCheckedCarByUserId(Integer userId);
}