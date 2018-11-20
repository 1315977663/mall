package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.Car;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.security.Auth;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
