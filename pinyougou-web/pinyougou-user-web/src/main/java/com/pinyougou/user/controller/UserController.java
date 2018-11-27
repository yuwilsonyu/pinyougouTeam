package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.springframework.web.bind.annotation.*;

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


}
