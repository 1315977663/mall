package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateFormatUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ICategoryService iCategoryService;

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
    public ServerResponse<ProductDetailVO> getDetail(int productId){
        if(productId < 0){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);

        return ServerResponse.createBySuccess(this.product2productDetailVO(product));
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

    @Override
    public ServerResponse<PageInfo<Product>> getPortalList(int categoryId, String keyword, int pageNum, int pageSize, String orderBy){
        List<Integer> categoryIds = iCategoryService.getDeepCategory(categoryId).getData(); // 存放品类 Id
        PageHelper.startPage(pageNum, pageSize);
        if(Const.ProductListOrderBy.orderByList.contains(orderBy)){
            String[] orderBy1 = orderBy.split("_");
            PageHelper.orderBy(orderBy1[0] + " "  + orderBy1[1]);
        }
        if(StringUtils.isBlank(keyword)){
            keyword = null;
        } else {
            keyword = "%" + keyword + "%";
        }
        List<Product> products = productMapper.getList(categoryIds,keyword);
        PageInfo<Product> pageInfo = new PageInfo<>(products);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductDetailVO product2productDetailVO(Product product) {
        if (product == null){
            return null;
        }
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVO.setParentCategoryId(0);
        } else {
            productDetailVO.setParentCategoryId(category.getParentId());
        }
        productDetailVO.setName(product.getName());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setImageHost(PropertiesUtil.getValue("imageHost", "www.baidu2.com"));
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setCreateTime(DateFormatUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateFormatUtil.dateToStr(product.getUpdateTime()));
        return productDetailVO;
    }

}
