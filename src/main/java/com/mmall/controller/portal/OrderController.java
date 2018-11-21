package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.Car;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.PageBean;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.jvm.hotspot.debugger.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    HttpSession session;

    @Autowired
    IOrderService iOrderService;

    @RequestMapping(value = "/create.do", method = RequestMethod.POST)
    @Auth(role = Role.CUSTOMER)
    public ServerResponse<OrderVo> create(Integer shippingId) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.createOrder(currentUser.getId(), shippingId);
    }

    @RequestMapping(value = "/get_order_cart_product.do", method = RequestMethod.GET)
    @Auth(role = Role.CUSTOMER)
    public ServerResponse getOrderCartProduct() {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.getOrderCartProduct(currentUser.getId());
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    @Auth(role = Role.CUSTOMER)
    public ServerResponse<PageBean> list(@RequestParam(defaultValue = "1")Integer pageNum,
                                                  @RequestParam(defaultValue = "10")Integer pageSize){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.list(currentUser.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.GET)
    @Auth(role = Role.CUSTOMER)
    public ServerResponse<OrderVo> detail(Long orderNo) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.detail(currentUser.getId(), orderNo);
    }

    @RequestMapping(value = "/cancel.do", method = RequestMethod.GET)
    @Auth(role = Role.CUSTOMER)
    public ServerResponse cancel(Long orderNo) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.cancel(currentUser.getId(), orderNo);
    }


    @RequestMapping("/pay.do")
    @Auth(role = Role.CUSTOMER)
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        String path = request.getSession().getServletContext().getRealPath("upload"); // 获取运行upload路径
        return iOrderService.pay(currentUser.getId(), orderNo, path);
    }

    @RequestMapping("/alpay_callback.do")
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String[]> requestParams = request.getParameterMap();
        ServerResponse serverResponse = iOrderService.alipayCallback(requestParams);
        if (serverResponse.isSuccess()) {
            return "success";
        }
        return serverResponse.getMessage();
    }

    @RequestMapping("/query_order_pay_status.do")
    @Auth(role = Role.CUSTOMER)
    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return iOrderService.queryOrderPayStatus(orderNo, user.getId());
    }
}
