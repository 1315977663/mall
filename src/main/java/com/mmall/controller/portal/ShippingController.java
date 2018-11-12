package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.IShippingService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * @program: mmall
 * @description: 收货地址
 * @author: fbl
 * @create: 2018-11-12 14:51
 **/
@RestController
@RequestMapping("/shipping")
@Auth(role = Role.CUSTOMER)
public class ShippingController {

    @Autowired
    HttpSession session;

    @Autowired
    IShippingService iShippingService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServerResponse<Map<String, Integer>> add (Shipping shipping) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iShippingService.add(currentUser.getId(), shipping);
    }

    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public ServerResponse del (Integer shippingId) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iShippingService.del(currentUser.getId(), shippingId);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ServerResponse update(Shipping shipping){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iShippingService.update(currentUser.getId(), shipping);
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public ServerResponse<Shipping>  select(Integer shippingId) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iShippingService.select(currentUser.getId(), shippingId);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<PageInfo<Shipping>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iShippingService.getListByUserId(currentUser.getId(), pageNum, pageSize);
    }

}
