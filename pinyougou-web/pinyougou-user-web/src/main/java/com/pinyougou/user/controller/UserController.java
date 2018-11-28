package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-17<p>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /** 注册用户 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String smsCode){
        try{
            // 判断短信验证码是否正确
            boolean pass = userService.checkSmsCode(smsCode, user.getPhone());
            if (pass) {
                userService.save(user);
            }
            return pass;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 发送短信验证码到用户手机*/
    @GetMapping("/sendCode")
    public boolean sendCode(String phone){
        try{
            return userService.sendSmsCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 更新用户密码*/
    @GetMapping("/updateUserpassword")
    public boolean updateUserpassword(String password){
        try{
            String username = httpServletRequest.getRemoteUser();
            return userService.updateUserpassword(username,password);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**获取登录用户手机号*/
    @GetMapping("/getUserPhone")
    public String getUserPhone(){
        try {
            String username = httpServletRequest.getRemoteUser();
            String phone = userService.getUserPhone(username);
            return phone;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /** 验证验证码是否正确*/
    @GetMapping("/checkCode")
    public boolean checkCode(String phone, String code){
        try{
            boolean ok = userService.checkSmsCode(code,phone);
            return ok;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 更新用户手机*/
    @GetMapping("/updateUserPhone")
    public boolean updateUserPhone (String newPhone,String code){
        try {
            String username = httpServletRequest.getRemoteUser();
            return userService.updateUserPhone(username,newPhone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
