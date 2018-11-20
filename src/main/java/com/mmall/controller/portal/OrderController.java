package com.mmall.controller.portal;

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
@Auth(role = Role.CUSTOMER)
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    HttpSession session;

    @Autowired
    IOrderService iOrderService;

    @RequestMapping("/pay.do")
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        String path = request.getSession().getServletContext().getRealPath("upload"); // 获取运行upload路径
        return iOrderService.pay(currentUser.getId(), orderNo, path);
    }

    @RequestMapping("/alpay_callback.do")
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String> valueMap = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        Iterator iter = requestParams.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.toString();
            String[] values = (String[]) requestParams.get(iter);
            String value = "";
            for (int i = 0; i < values.length; i++) {
                value = (i == values.length - 1) ? value + values[i] : value + values[i] + ",";
            }
            valueMap.put(name, value);
        }
        logger.info("支付宝回掉的参数集合：{}。sign：{}。sign_type：{}", valueMap.toString(), valueMap.get("sign"), valueMap.get("sign_type"));
        
        return null;
    }
}
