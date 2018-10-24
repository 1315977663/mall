package com.mmall.service;

import com.mmall.common.PageBean;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

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
}
