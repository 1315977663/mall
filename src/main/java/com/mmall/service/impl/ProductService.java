package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmall.common.Const;
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

    @Override
    public ServerResponse<Product> getDetail(int productId){
        if(productId < 0){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        return ServerResponse.createBySuccess(product);
    }

    @Override
    public ServerResponse setSaleStatus(int productId, int status){

        if(productId < 0){
            return ServerResponse.createByErrorMessage("id参数错误");
        }

        if(status < Const.ProductStatus.SELLING || status > Const.ProductStatus.DELETE){
            return ServerResponse.createByErrorMessage("status参数错误");
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int result = productMapper.updateByPrimaryKeySelective(product);
        if(result > 0){
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }

        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    @Override
    public ServerResponse save(Product product){
        if(product.getId() == null){
            // 执行新增操作
            int result = productMapper.insertSelective(product);
            if(result > 0){
                return ServerResponse.createBySuccessMessage("新增产品成功");
            }
            return ServerResponse.createByErrorMessage("新增产品失败");
        }
        // 执行更新操作
        int result = productMapper.updateByPrimaryKeySelective(product);
        if(result > 0){
            return ServerResponse.createBySuccessMessage("更新产品信息成功");
        }
        return ServerResponse.createByErrorMessage("更新产品失败");
    }

}
