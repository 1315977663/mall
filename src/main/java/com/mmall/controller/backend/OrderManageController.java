package com.mmall.controller.backend;

import com.mmall.common.PageBean;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.security.Auth;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderVo;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-21 18:07
 **/
@RestController
@RequestMapping("/manage/order")
@Auth(role = Role.ADMIN)
public class OrderManageController {

    @Autowired
    IOrderService iOrderService;

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse<PageBean> list(@RequestParam(defaultValue = "1")Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return iOrderService.list(null, pageNum, pageSize);
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.GET)
    public ServerResponse<OrderVo> detail(Long orderNo){
        return iOrderService.detail(null, orderNo);
    }

    @RequestMapping(value = "/send_goods.do", method = RequestMethod.GET)
    public ServerResponse sendGoods(Long orderNo) {
        return iOrderService.sendGoods(orderNo);
    }

    @RequestMapping(value = "/search.do")
    public ServerResponse<PageBean> search(@RequestParam(defaultValue = "0")Long likeOrderNo,
                                           @RequestParam(defaultValue = "1")Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize){
        return iOrderService.search(likeOrderNo, pageNum, pageSize);
    }
}
