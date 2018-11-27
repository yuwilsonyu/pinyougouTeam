package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-19<p>
 */
@RestController
public class LoginController {

    @GetMapping("/user/showName")
    public Map<String, String> showName(){
        // 获取SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // 获取用户名
        String loginName = securityContext.getAuthentication().getName();

        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }
}
