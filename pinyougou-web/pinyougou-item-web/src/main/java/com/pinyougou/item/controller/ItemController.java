package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 商品控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-15<p>
 */
@Controller
public class ItemController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * http://item.pinyougou.com/SPU商品goodsId.html
     * {goodsId} : 取请求URL中的变量
     */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId")Long goodsId, Model model){
        System.out.println("goodsId: " + goodsId);

        // 根据SPU的id查询商品信息
        Map<String,Object> dataModel = goodsService.getGoods(goodsId);

        // 数据模型
        model.addAllAttributes(dataModel);

        return "item";
    }

}
