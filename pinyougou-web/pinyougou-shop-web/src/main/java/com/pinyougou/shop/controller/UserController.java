package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    //引用sellerService服务
    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/updateSellerPassword")
    public boolean updateSellerPassword(String oldPassword, String newPassword) {
        ///获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建map集合封装数据
        try {
            // 查询当前账户的密码是否正确
            Seller seller = sellerService.checkPassword(username);
            //对比密码
            boolean ok = passwordEncoder.matches(oldPassword, seller.getPassword());
                //加密密码
                newPassword = passwordEncoder.encode(newPassword);
                sellerService.updateSellerPasword(username, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
