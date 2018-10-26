package com.mmall.controller.backend;

import com.mmall.common.Role;
import com.mmall.common.ServerResponse;
import com.mmall.security.Auth;
import com.mmall.service.IBaseCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: mmall
 * @description: 后台统计接口
 * @author: fbl
 * @create: 2018-10-26 10:01
 **/
@RestController
@RequestMapping("/manage/statistic")
@Auth(role = Role.ADMIN)
public class StatisticManageController {

    @Autowired
    IBaseCountService iBaseCountService;

    @RequestMapping(value = "/base_count", method = RequestMethod.GET)
    public ServerResponse<Map<String, Integer>> baseCount(){
        return iBaseCountService.baseCount();
    }

}
