package com.mmall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateFormatUtil;
import com.mmall.vo.OrderCarProductVo;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderService implements IOrderService {

    private static final Logger  log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    @Transactional
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCheckedCarByUserId(userId);
        List<OrderItem> orderItemList = Lists.newArrayList();
        Long orderNo = System.currentTimeMillis() + new Random().nextInt(100);
        List<String> productIds = Lists.newArrayList();
        BigDecimal payment = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (Cart cart : cartList) {
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product == null) {
                return ServerResponse.createByErrorMessage("商品不存在");
            }
            if (product.getStatus() != Const.ProductStatus.SELLING) {
                return ServerResponse.createByErrorMessage(String.format("商品%s已下架", product.getName()));
            }
            if (product.getStock() < cart.getQuantity()) {
                return ServerResponse.createByErrorMessage(String.format("商品%s库存不足", product.getName()));
            } else {
                // 减少库存
                product.setStock(product.getStock() - cart.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
            // 每个订单item 总价格
            BigDecimal totalPrice = BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(totalPrice);
            orderItem.setUserId(userId);

            orderItemList.add(orderItem);
            productIds.add(product.getId().toString());

            // 订单总 价格
            payment = BigDecimalUtil.add(payment.doubleValue(), totalPrice.doubleValue());
        }
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setPayment(payment);
        order.setStatus(Const.OrderStatus.NOT_PAYING.getCode());
        order.setPaymentType(Const.PaymentType.ONLINE.getCode());
        order.setPostage(10);
        order.setShippingId(shippingId);
        order.setUserId(userId);

        orderMapper.insertSelective(order);

        orderItemMapper.inserts(orderItemList);

        // 清空购物车
        cartMapper.deleteByProducts(userId, productIds);

        // 返回数据
        Order nowOrder = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        return this.order2OrderVo(nowOrder);
    }

    @Override
    public ServerResponse<OrderCarProductVo> getOrderCartProduct(Integer userId) {

        List<OrderItem> orderItems = orderItemMapper.selectByUserId(userId);
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal productTotalPrice = BigDecimal.ZERO;

        for(OrderItem orderItem : orderItems) {
            orderItemVoList.add(this.orderItem2OrderItemVo(orderItem).getData());
            productTotalPrice = BigDecimalUtil.add(productTotalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        OrderCarProductVo orderCarProductVo = new OrderCarProductVo();
        orderCarProductVo.setOrderItemVoList(orderItemVoList);
        orderCarProductVo.setProductTotalPrice(productTotalPrice);

        return ServerResponse.createBySuccess(orderCarProductVo);

    }

    @Override
    public ServerResponse<PageBean> list(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectList(userId);
        List<OrderVo> orderVos = Lists.newArrayList();
        for (Order order : orders) {
            orderVos.add(this.order2OrderVo(order).getData());
        }
        PageBean pageBean = new PageBean<>(orders);
        pageBean.setList(orderVos); // 更换pageBean 里的列表
        return ServerResponse.createBySuccess(pageBean);
    }

    @Override
    public ServerResponse<OrderVo> detail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        OrderVo orderVo = this.order2OrderVo(order).getData();
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("您没有此订单");
        }
        if (order.getStatus() != Const.OrderStatus.NOT_PAYING.getCode()) {
            return ServerResponse.createByErrorMessage("订单状态不是未付款");
        }
        order.setStatus(Const.OrderStatus.CANCELLED.getCode());
        int cont = orderMapper.updateByPrimaryKeySelective(order);
        if (cont == 0) {
            return ServerResponse.createByErrorMessage("取消失败");
        }
        return ServerResponse.createBySuccessMessage("取消成功");
    }

    @Override
    public ServerResponse<PageBean> search(Long likeOrderNo, Integer pageNum, Integer pageSize) {
        if (likeOrderNo == 0) {
            return this.list(null, pageNum, pageSize);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectByLikeOrderNo("%" + likeOrderNo +  "%");
        List<OrderVo> orderVos = Lists.newArrayList();
        for (Order order : orders) {
            orderVos.add(this.order2OrderVo(order).getData());
        }
        PageBean pageBean = new PageBean<>(orders);
        pageBean.setList(orderVos); // 更换pageBean 里的列表
        return ServerResponse.createBySuccess(pageBean);
    }

    @Override
    public ServerResponse sendGoods(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("您没有此订单");
        }
        if (order.getStatus() != Const.OrderStatus.PAYED.getCode()) {
            return ServerResponse.createByErrorMessage("订单状态不能发货");
        }
        order.setStatus(Const.OrderStatus.SHIPPED.getCode());
        order.setSendTime(new Date(System.currentTimeMillis()));
        int cont = orderMapper.updateByPrimaryKeySelective(order);
        if (cont == 0) {
            return ServerResponse.createByErrorMessage("发货失败");
        }
        return ServerResponse.createBySuccessMessage("发货成功");
    }


    private ServerResponse<OrderVo> order2OrderVo(Order order){
        OrderVo orderVo = new OrderVo();

        orderVo.setUserId(order.getUserId());
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentType.getName(order.getPaymentType()));
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatus.getMsg(order.getStatus()));
        orderVo.setPostage(order.getPostage());
        orderVo.setPaymentTime(DateFormatUtil.dateToStr(order.getPaymentTime()));
        orderVo.setCloseTime(DateFormatUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(DateFormatUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateFormatUtil.dateToStr(order.getSendTime()));
        orderVo.setCreateTime(DateFormatUtil.dateToStr(order.getCreateTime()));


        List<OrderItem> orderItemList = orderItemMapper.getListByOrderNo(order.getOrderNo());
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for (OrderItem orderItem : orderItemList) {
            orderItemVoList.add(this.orderItem2OrderItemVo(orderItem).getData());
        }

        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setShippingId(order.getShippingId());

        Shipping shipping =  shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping == null) {
            log.error("此地址不存在：{}", order.getShippingId());
            shipping = new Shipping();
        }

        orderVo.setShippingVo(this.shipping2ShippingVo(shipping).getData());


        return ServerResponse.createBySuccess(orderVo);
    }

    private ServerResponse<OrderItemVo> orderItem2OrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();

        Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());

        if (product == null) {
            orderItemVo.setProductId(0);
            orderItemVo.setCurrentUnitPrice(BigDecimal.ZERO);
            orderItemVo.setProductImage("暂无商品信息");
            orderItemVo.setProductName("暂无商品信息");
        } else {
            orderItemVo.setProductId(product.getId());
            orderItemVo.setCurrentUnitPrice(product.getPrice());
            orderItemVo.setProductImage(product.getMainImage());
            orderItemVo.setProductName(product.getName());
        }

        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreate(DateFormatUtil.dateToStr(orderItem.getCreateTime()));

        return ServerResponse.createBySuccess(orderItemVo);

    }

    private ServerResponse<ShippingVo> shipping2ShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverName());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return ServerResponse.createBySuccess(shippingVo);
    }



    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String path){

        // 校验userID， orderNO是否存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户不存在次订单");
        }
        Map<String, String> resultMap = Maps.newHashMap();
        resultMap.put("orderNo", String.valueOf(orderNo));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(orderNo);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "商场订单号：" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "快乐商场的商品";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        List<OrderItem> orderItems = orderItemMapper.getListByOrderNo(orderNo);

        for (OrderItem orderItem : orderItems) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderNo.toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), 100d).longValue(),
                    orderItem.getQuantity());

            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goodsDetail);
        }

        Configs.init("zfbinfo.properties");
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://gzay7c.natappfree.cc/order/alpay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File file = new File(path);
                if(!file.exists()){
                    file.setWritable(true);
                    file.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path+"/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                resultMap.put("path", filePath);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    @Override
    public ServerResponse alipayCallback(Map<String, String[]> requestParams) {
        // 重新组装参数Map
        Map<String , String> params = Maps.newHashMap();
        for(Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String value = "";
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                value = (i == values.length - 1) ? value + values[i] : value + values[i] + ",";
            }
            params.put(entry.getKey(), value);
        }
        log.info("sign：{}。trade_status：{}。支付宝回掉的参数集合：{}。", params.get("sign"), params.get("trade_status"), params);

//        params.remove("sign_type");

        try {
            if (!AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType())) {
                log.error("支付宝回调验证签名失败");
                return ServerResponse.createByErrorMessage("非法请求，请勿再请求，否则报警");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证签名异常：{}", e);
            return ServerResponse.createByErrorMessage("支付宝签名验证异常");
        }

        String orderNo = params.get("out_trade_no");
        String trade_status = params.get("trade_status");
        String total_amount = params.get("total_amount");
        String appId = params.get("app_id");
        String paymentTime = params.get("gmt_payment");
        String trade_no = params.get("trade_no");

        // 查询平台是否存在订单
        Order order = orderMapper.selectByOrderNo(Long.valueOf(orderNo));
        if(order == null) {
            log.error("平台不存在订单：{}", orderNo);
            return ServerResponse.createByErrorMessage("支付宝签名验证异常");
        }

        // 核查商户id
        if(!Configs.getAppid().toString().equals(appId)) {
            log.error("商户appid不正确{}", appId);
            return ServerResponse.createByErrorMessage("商户appId不正确");
        }

        //核查订单总价
        BigDecimal payment = new BigDecimal(total_amount);
        if (payment.compareTo(order.getPayment()) != 0) {
            log.error("与平台订单总价不符,平台订单总价{},回调参数{}",order.getPayment().toString(), payment.toString());
            return ServerResponse.createByErrorMessage("与平台订单总价不符");
        }

        // 如果订单是已付款状态，直接返回success。
        // 如果是取消状态也要进行订单状态更新为已付款。
        if (order.getStatus() > Const.OrderStatus.NOT_PAYING.getCode()) {
            return ServerResponse.createBySuccess();
        }

        if (StringUtils.equals(trade_status, "TRADE_SUCCESS") || StringUtils.equals(trade_status, "TRADE_FINISHED")) {
            // 更新订单状态为已支付(包括取消的订单在此时收到了支付成功，也要更新支付状态)
            order.setStatus(Const.OrderStatus.PAYED.getCode());
            order.setPaymentTime(DateFormatUtil.strToDate(paymentTime));
            int result = orderMapper.updateByPrimaryKeySelective(order);
            if (result <= 0) {
                log.error("订单状态更新失败，订单号：{}，支付状态：{}，支付时间：{}，支付平台：{}",
                        orderNo, trade_status, paymentTime, Const.PayPlatform.ALIPAY.getName());  // 运维时通过日志补失败的更新。
            }
        }
        // 插入交易记录
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(Long.valueOf(orderNo));
        payInfo.setPlatformStatus(trade_status);
        payInfo.setPlatformNumber(trade_no);
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
        int insertResult = payInfoMapper.insertSelective(payInfo);
        if (insertResult == 0){
            log.error("交易记录插入失败。订单号：{},用户id：{},支付状态：{},支付宝订单号",
                    orderNo, order.getUserId(), trade_status, trade_no); // 运维时通过日志补失败的更新。
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo, Integer userId){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户不存在此订单");
        }
        if (order.getStatus() > Const.OrderStatus.NOT_PAYING.getCode()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
