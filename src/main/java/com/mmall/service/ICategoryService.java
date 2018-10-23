package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-23 10:45
 **/
public interface ICategoryService {
    ServerResponse<List<Category>> getCategory(int categoryId);

    ServerResponse addCategory(int categoryId, String categoryName);

    ServerResponse setCategoryName(int categoryId, String categoryName);

    ServerResponse<List<Integer>> getDeepCategory(int categoryId);
}
