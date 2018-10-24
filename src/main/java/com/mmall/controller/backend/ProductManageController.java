package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.PageBean;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @program: mmall
 * @description: 后台商品管理接口
 * @author: fbl
 * @create: 2018-10-24 15:25
 **/
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    IProductService iProductService;

    @Autowired
    HttpSession session;

    @Auth(role = Role.CUSTOMER)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<PageBean<Product>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize){

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return iProductService.getList(pageNum, pageSize);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ServerResponse<PageBean<Product>> search() {

        User currentUser = (User) this.session.getAttribute(Const.CURRENT_USER);

        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("没有登录");
        }
        if(currentUser.getRole() != Const.Role.ROLE_ADMIN) {
            return  ServerResponse.createByErrorMessage("没有权限");
        }

        return null;
    }

}
