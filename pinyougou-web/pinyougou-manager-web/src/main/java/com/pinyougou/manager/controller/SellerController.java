package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

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
