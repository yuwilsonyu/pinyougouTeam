package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-09<p>
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /** 查询全部商家待审核的商品 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows){
        try{
            if (goods != null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        // 设置审核状态(未审核)
        goods.setAuditStatus("0");
        // 分页查询
        return goodsService.findByPage(goods, page, rows);
    }

    /** 商品审核(修改商品状态) */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status){
        try{
            goodsService.updateStatus(ids, status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 批量删除 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            goodsService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
