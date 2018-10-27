package com.mmall.controller.backend;

import com.mmall.common.PageBean;
import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.security.Auth;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * @program: mmall
 * @description: 后台商品管理接口
 * @author: fbl
 * @create: 2018-10-24 15:25
 **/
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    IProductService iProductService;

    @Autowired
    IFileService iFileService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ServerResponse<PageBean<Product>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize){
        return iProductService.getList(pageNum, pageSize);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ServerResponse<Object> search(@RequestParam(defaultValue = "-1") int productId,
                                                    @RequestParam(defaultValue = "") String productName,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize){
        return iProductService.search(productId, productName, pageNum, pageSize);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ServerResponse<ProductDetailVO> detail(@RequestParam(defaultValue = "-1") int productId){
        return iProductService.getDetail(productId);
    }

    /**
     *  默认上架状态
     * @param productId 产品id
     * @param status 1->上架 2->下架 3->删除
     * @return
     */
    @RequestMapping(value = "/set_sale_status", method = RequestMethod.PUT)
    public ServerResponse setSaleStatus(@RequestParam(defaultValue = "-1") int productId,
                                        @RequestParam(defaultValue = "1") int status){
        return iProductService.setSaleStatus(productId, status);
    }


    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public ServerResponse save(Product product){
        return iProductService.save(product);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ServerResponse<String> upload(@RequestParam(value = "file") MultipartFile multipartFile, String submit){
        return iFileService.upload(multipartFile, submit);
    }

}
