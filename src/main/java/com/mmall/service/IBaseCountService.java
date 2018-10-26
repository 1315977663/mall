package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-26 10:11
 **/
public interface IBaseCountService {
    ServerResponse<Map<String, Integer>> baseCount();
}
