package com.mmall.service.impl;

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
            return ServerResponse.createBySuccess("增加类型成功");
        }
        return ServerResponse.createByErrorMessage("增加失败");
    }

}
