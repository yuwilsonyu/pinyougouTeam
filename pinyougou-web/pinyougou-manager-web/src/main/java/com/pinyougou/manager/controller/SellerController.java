package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

/**
 * 商家控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-05<p>
 */
/*
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;
合适
    */
/** 查询待审核商家列表 *//*

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, Integer rows){
        try{
            */
/** GET请求转码 *//*

            if (seller != null && StringUtils.isNoneBlank(seller.getName())){
                seller.setName(new String(seller.getName().getBytes("ISO8859-1"), "UTF-8"));
            }
            if (seller != null && StringUtils.isNoneBlank(seller.getNickName())){
                seller.setNickName(new String(seller.getNickName().getBytes("ISO8859-1"), "UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return sellerService.findByPage(seller, page, rows);
    }

    */
/** 商家审核 *//*

    @GetMapping("/updateStatus")
    public boolean updateStatus(String sellerId, String status){
        try{
            sellerService.updateStatus(sellerId, status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  false;
    }
}
*/
@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference(timeout = 10000)
    private SellerService sellerService;
    /** 商家审核与商家管理用到*/
    @RequestMapping("/findByPage")
    public PageResult findByPage(Integer page, Integer rows, Seller seller){
//        seller.setStatus("0");
        try {
            if (seller != null && StringUtils.isNoneBlank(seller.getName())) {
                seller.setName("%"+(new String(seller.getName().getBytes("ISO8859-1"), "UTF-8"))+"%");
            }
            if (seller != null && StringUtils.isNoneBlank(seller.getNickName())) {
                seller.setNickName("%"+(new String(seller.getNickName().getBytes("ISO8859-1"), "UTF-8"))+"%");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sellerService.findByPage(seller,page,rows);
    }

    @RequestMapping("/updateStatus")
    public boolean updateStatus(String sellerId, String status){
        try {
            Seller seller = new Seller();
            seller.setStatus(status);
            seller.setSellerId(sellerId);
            sellerService.update(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

