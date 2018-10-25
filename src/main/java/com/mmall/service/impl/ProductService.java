package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-24 15:34
 **/
@Service("iProductService")
public class ProductService implements IProductService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse<PageBean<Product>> getList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.getAllList();
        PageBean<Product> pageBean = new PageBean<>(productList);
        return ServerResponse.createBySuccess(pageBean);
    }

    @Override
    public ServerResponse<Object> search(int productId, String productName,
                                                    int pageNum, int pageSize){

        if(productId != -1){
            Product product = productMapper.selectByPrimaryKey(productId);
            return ServerResponse.createBySuccess(product);
        }
         if(!StringUtils.isBlank(productName)){
            PageHelper.startPage(pageNum, pageSize);
            List<Product> products = productMapper.selectByLikeProductName("%" + productName + "%");
            PageBean<Product> pageBean = new PageBean<>(products);
            return ServerResponse.createBySuccess(pageBean);
         }


        return ServerResponse.createByErrorMessage("参数错误");
    }

}
