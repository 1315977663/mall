package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
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
import java.util.Iterator;
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
        Map<String, String> valueMap = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String value = "";
            for (int i = 0; i < values.length; i++) {
                value = (i == values.length - 1) ? value + values[i] : value + values[i] + ",";
            }
            valueMap.put(name, value);
        }
        logger.info("支付宝回掉的参数集合：{}。sign：{}。trade_status：{}", valueMap.toString(), valueMap.get("sign"), valueMap.get("trade_status"));
        // 验证回调的正确性，防止重复回调
        // 验签需要移除sign,sign_type两个参数，支付宝sdk中移除了sign
        valueMap.remove("sign_type");
        try {
            // 支付宝sdk提供的验签API
            boolean alipaySignature = AlipaySignature.rsaCheckV2(valueMap, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipaySignature) {
                return "验证不通过，非法请求";
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常{}", e);
        }

        ServerResponse serverResponse = iOrderService.alipayCallback(valueMap);
        if (serverResponse.isSuccess()) {
            return "success";
        }

        return "filed";
    }
}
