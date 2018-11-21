package com.mmall.service;

import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderCarProductVo;
import com.mmall.vo.OrderVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface IOrderService {
    @Transactional
    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    ServerResponse<OrderCarProductVo> getOrderCartProduct(Integer userId);

    ServerResponse<PageBean> list(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<OrderVo> detail(Integer userId, Long orderNo);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse<PageBean> search(Long likeOrderNo, Integer pageNum, Integer pageSize);

    ServerResponse sendGoods(Long orderNo);

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse alipayCallback(Map<String, String[]> requestParams);

    ServerResponse<Boolean> queryOrderPayStatus(Long orderNo, Integer userId);
}
