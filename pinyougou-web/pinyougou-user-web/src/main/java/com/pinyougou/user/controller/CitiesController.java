package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Cities;
import com.pinyougou.service.CitiesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CitiesController {
    @Reference(timeout = 10000)
    private CitiesService citiesService;

    @GetMapping("/findCityId")
    public List<Cities> findCityID(Long provinceId){
        return citiesService.findCityByProvinceId(provinceId);
    }

}
