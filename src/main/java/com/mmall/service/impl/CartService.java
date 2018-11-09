package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-08 16:44
 **/
@Service("iCartService")
public class CartService implements ICartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){

        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);

        if(cart == null){
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.CartStatus.CHECKED); //  默认已选中
            cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
       Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
       if(cart != null){
           cart.setQuantity(count);
           cartMapper.updateByPrimaryKeySelective(cart);
       }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String products){
        List<String> productList = Splitter.on(',').splitToList(products);
        cartMapper.deleteByProducts(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> changeCheck(Integer userId, Integer productId, int checkStatus){
        cartMapper.changeCheck(userId, productId, checkStatus);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCount(Integer userId){
        return ServerResponse.createBySuccess(cartMapper.getCountByUserId(userId));
    }


    @Override
    public ServerResponse<CartVo> list(Integer userId){
        return ServerResponse.createBySuccess(this.getCartVoLimit(userId));
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> carts = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVos = Lists.newArrayList();
        BigDecimal totalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(carts)){
            for(Cart c : carts){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(c.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(c.getProductId());
                cartProductVo.setProductChecked(c.getChecked());
                cartProductVo.setQuantity(c.getQuantity());
                Product product = productMapper.selectByPrimaryKey(c.getProductId());
                if(product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());

                    if(cartProductVo.getQuantity() <= cartProductVo.getProductStock()){
                        cartProductVo.setLimitQuantity(Const.CartStatus.LIMIT_NUM_SUCCESS);
                    } else{
                        cartProductVo.setLimitQuantity(Const.CartStatus.LIMIT_NUM_FAIL);
                        cartProductVo.setQuantity(cartProductVo.getProductStock());
                        //更新有效库存
                        Cart cart = new Cart();
                        cart.setId(c.getId());
                        cart.setQuantity(cartProductVo.getQuantity());
                        cartMapper.updateByPrimaryKeySelective(cart);
                    }

                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity(), cartProductVo.getProductPrice().doubleValue()));
                }
                if(cartProductVo.getProductChecked() == Const.CartStatus.CHECKED) {
                    totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVos.add(cartProductVo);
            }
        }

        cartVo.setAllChecked(this.isAllChecked(userId));
        cartVo.setCartProductVoList(cartProductVos);
        cartVo.setCartTotalPrice(totalPrice);

        return cartVo;
    }

    private boolean isAllChecked(Integer userId){
        if (userId == null) {
            return false;
        }
        return cartMapper.allNnChecked(userId) == 0; // 没有选中的为0 ，则是全选
    }
}
