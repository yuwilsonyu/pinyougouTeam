package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.ProvincesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/province")
public class ProvinceController {
    /** 定义商品分类服务接口 */
    @Reference(timeout=10000)
    private ProvincesService provincesService;
    /** 根据父级id查询商品分类 */
    @GetMapping("/findProvinceId")
    public List<Provinces> findItemCatByParentId(){
        return provincesService.findAll();
    }


}
