package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-05<p>
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** 申请入驻 */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try{
            // 密码加密
            String password = passwordEncoder.encode(seller.getPassword());
            //封装数据
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }



    @GetMapping("/Merchant")
    public Seller Merchant(){
        try {
            //获取当前用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //查询数据
            Seller seller = sellerService.checkPassword(username);
            return seller;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //保存商家信息
    @PostMapping("/SaveOrbusiness")
    public boolean SaveOrbusiness(@RequestBody Seller seller){
        try{
            //保存信息，不知道要处理什么
            sellerService.update(seller);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

}
