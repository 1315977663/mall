package com.mmall.service.impl;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.UserMapper;
import com.mmall.service.IBaseCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-26 10:12
 **/
@Service("iBaseCountService")
public class BaseCountService implements IBaseCountService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;


    @Override
    public ServerResponse<Map<String, Integer>> baseCount(){
        Map<String, Integer> countMap = Maps.newHashMapWithExpectedSize(3);

        countMap.put("userCount", userMapper.selectUserCount());
        countMap.put("productCount", productMapper.selectCount());
        countMap.put("orderCount", orderMapper.selectCount());

        return ServerResponse.createBySuccess(countMap);
    }

}
