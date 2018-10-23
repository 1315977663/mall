package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @program: mmall
 * @description: 后台种类管理
 * @author: fbl
 * @create: 2018-10-23 10:40
 **/
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    ICategoryService iCategoryService;

    @Autowired
    HttpSession session;

    @RequestMapping("/get_category")
    public ServerResponse<List<Category>> getCategory(@RequestParam(defaultValue = "0")int categoryId, HttpSession session) {

        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.getCategory(categoryId);
    }

    @RequestMapping("/add_category")
    public ServerResponse addCategory(@RequestParam(defaultValue = "0")int parentId, String categoryName) {

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.addCategory(parentId, categoryName);
    }

    @RequestMapping("/set_category_name")
    public ServerResponse setCategoryName(int categoryId, String categoryName) {

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping("get_deep_category")
    public ServerResponse<List<Integer>> getDeepCategory(@RequestParam(defaultValue = "0") int categoryId){

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.getDeepCategory(categoryId);
    }
}
