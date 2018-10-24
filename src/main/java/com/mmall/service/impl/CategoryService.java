package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-23 10:47
 **/
@Service("iCategoryService")
public class CategoryService implements ICategoryService {
    Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse<List<Category>> getCategory(int categoryId) {
        List<Category> categories = categoryMapper.getCategoryByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(categories)) {
            logger.warn("{}该类型没有数据", categoryId);
        }
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse addCategory(int parentId, String categoryName) {
        if(StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("种类名称不能为空");
        }
        if(parentId != 0) {
            Category parent = categoryMapper.selectByPrimaryKey(parentId);
            if (parent == null) {
                return ServerResponse.createByErrorMessage("没有此父类型");
            }
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setStatus(true);
        category.setParentId(parentId);
        int result = categoryMapper.insertSelective(category);
        if (result > 0) {
            return ServerResponse.createBySuccessMessage("增加类型成功");
        }
        return ServerResponse.createByErrorMessage("增加失败");
    }

    @Override
    public ServerResponse setCategoryName(int categoryId, String categoryName) {
        if(StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改类型名称不能为空");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int result = categoryMapper.updateByPrimaryKeySelective(category);
        if (result > 0){
            return ServerResponse.createBySuccessMessage("修改类型名称成功");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategory(int categoryId){
        Set<Category> set = Sets.newHashSet();
        this.setCategory(set, categoryId);
        List<Integer> list = Lists.newArrayList();
        for(Category category : set){
            list.add(category.getId());
        }
        list.add(categoryId);
        return ServerResponse.createBySuccess(list);
    }

    private Set<Category> setCategory(Set<Category> set, int categoryId){
        List<Category> categories = this.getCategory(categoryId).getData();
        for(Category category : categories){
            set.add(category);
            setCategory(set, category.getId());
        }

        return set;
    }
}
