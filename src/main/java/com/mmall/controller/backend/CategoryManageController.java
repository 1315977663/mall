package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/get_category", method = RequestMethod.GET)
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

    @RequestMapping(value = "/add_category", method = RequestMethod.POST)
    public ServerResponse addCategory(@RequestParam(defaultValue = "0")int parentId, String categoryName, HttpServletRequest servletRequest) {

        System.out.println(servletRequest.getMethod());

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.addCategory(parentId, categoryName);
    }

    @RequestMapping(value = "/set_category_name", method = RequestMethod.PUT)
    public ServerResponse setCategoryName(Integer categoryId, String categoryName, HttpServletRequest servletRequest) {

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iCategoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "get_deep_category", method = RequestMethod.GET)
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
