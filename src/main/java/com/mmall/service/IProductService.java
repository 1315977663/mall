package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-24 15:33
 **/
public interface IProductService {
    ServerResponse<PageBean<Product>> getList(int pageNum, int pageSize);

    ServerResponse<Object> search(int productId, String productName,
                                  int pageNum, int pageSize);

    ServerResponse<ProductDetailVO> getDetail(int productId);

    ServerResponse setSaleStatus(int productId, int status);

    ServerResponse save(Product product);

    ServerResponse<PageInfo<Product>> getPortalList(int categoryId, String keyword, int pageNum, int pageSize, String orderBy);
}
