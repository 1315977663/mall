package com.mmall.controller.backend;

import com.mmall.common.PageBean;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.security.Auth;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description: 后台商品管理接口
 * @author: fbl
 * @create: 2018-10-24 15:25
 **/
@Auth(role = Role.ADMIN)
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    IProductService iProductService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<PageBean<Product>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize){
        return iProductService.getList(pageNum, pageSize);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ServerResponse<Object> search(@RequestParam(defaultValue = "-1") int productId,
                                                    @RequestParam(defaultValue = "") String productName,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize){
        return iProductService.search(productId, productName, pageNum, pageSize);
    }

}
