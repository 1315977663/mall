package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-11-12 15:07
 **/
@Service("iShippingService")
public class ShippingService implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Map<String, Integer>> add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int result = shippingMapper.insert(shipping);
        if (result == 0) {
            return ServerResponse.createByErrorMessage("新增失败");
        }
        Map<String, Integer> data = Maps.newHashMap();
        data.put("shippingId", shipping.getId());
        return ServerResponse.createBySuccess(data, "新增成功");
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        int cont = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (cont > 0) {
            return ServerResponse.createBySuccessMessage("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int cont = shippingMapper.updateByUserIdAndId(shipping);
        if (cont > 0) {
            return ServerResponse.createBySuccessMessage("修改成功");
        }
        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdAndId(userId, shippingId);
        if (shipping != null) {
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }

    @Override
    public ServerResponse<PageInfo<Shipping>> getListByUserId(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageNum);
        List<Shipping> shippings = shippingMapper.getListByUserId(userId);
        PageInfo<Shipping> pageInfo = new PageInfo<>(shippings);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
