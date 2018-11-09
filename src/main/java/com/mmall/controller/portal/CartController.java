package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/car")
@Auth(role = Role.CUSTOMER)
public class CartController {

    @Autowired
    HttpSession session;

    @Autowired
    ICartService iCartService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServerResponse<CartVo> add(Integer productId, Integer count){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.add(currentUser.getId(), productId, count);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ServerResponse<CartVo> update(Integer productId, Integer count){
        User currentUser = (User) session.getAttribute((Const.CURRENT_USER));
        return iCartService.update(currentUser.getId(), productId, count);
    }

    @RequestMapping(value = "/delete_product", method = RequestMethod.POST)
    public ServerResponse<CartVo> deleteProduct(String productIds){
        User currentUser = (User) session.getAttribute((Const.CURRENT_USER));
        return iCartService.deleteProduct(currentUser.getId(), productIds);
    }

//    /cart/select.do
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public ServerResponse<CartVo> select(Integer productId){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.changeCheck(currentUser.getId(), productId, Const.CartStatus.CHECKED);
    }

    @RequestMapping(value = "/un_select", method = RequestMethod.POST)
    public ServerResponse<CartVo> unSelect(Integer productId){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.changeCheck(currentUser.getId(), productId, Const.CartStatus.UN_CHECK);
    }

    @RequestMapping(value = "/select_all", method = RequestMethod.POST)
    public ServerResponse<CartVo> selectAll(){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.changeCheck(currentUser.getId(), null, Const.CartStatus.CHECKED);
    }

    @RequestMapping(value = "/un_select_all", method = RequestMethod.POST)
    public ServerResponse<CartVo> unSelectAll(){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.changeCheck(currentUser.getId(), null, Const.CartStatus.UN_CHECK);
    }
//get_cart_product_count
    @RequestMapping(value = "/get_cart_product_count", method = RequestMethod.GET)
    public ServerResponse<Integer> getCartProductCount(){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.getCount(currentUser.getId());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<CartVo> list(){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iCartService.list(currentUser.getId());
    }
}
