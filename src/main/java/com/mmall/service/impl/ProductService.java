package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
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

}
