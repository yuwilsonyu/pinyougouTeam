package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户服务类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-05<p>
 */
public class UserDetialsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("sellerService: " + sellerService);
        System.out.println("username: " + username);
        // 从tb_seller表查询数据
        Seller seller = sellerService.findOne(username);
        // 商家不为空，并且 审核通过的商家
        if (seller != null && "1".equals(seller.getStatus())){
            /** 定义List集合封装角色 */
            List<GrantedAuthority> authorities = new ArrayList<>();
            // 添加角色
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

            return new User(username, seller.getPassword(), authorities);
        }
        return null;
    }

    /** spring的setter注入 */
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
