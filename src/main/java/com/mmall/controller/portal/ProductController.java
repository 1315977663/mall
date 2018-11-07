package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    IProductService iProductService;

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ServerResponse<ProductDetailVO> detail(int productId){
        return iProductService.getDetail(productId);
    }

    /**
     * categoryId
     * keyword
     * pageNum(default=1)
     * pageSize(default=10)
     * orderBy(default="")：排序参数：例如price_desc，price_asc
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<PageInfo<Product>> list(@RequestParam(defaultValue = "0") int categoryId,
                                                  @RequestParam(defaultValue = "") String keyword,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(defaultValue = "") String orderBy){
        return iProductService.getPortalList(categoryId, keyword, pageNum, pageSize, orderBy);
    }
}
