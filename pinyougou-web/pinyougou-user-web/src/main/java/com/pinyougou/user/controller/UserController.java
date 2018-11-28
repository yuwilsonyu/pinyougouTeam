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

    /** 注册用户 */
    @PostMapping("/update")
    public boolean update(@RequestBody User user){
//        {"headPic":"xxxx","nickName":"abab","sex":"男","birthday":"2018-10-31","provinceId":"430000","cityId":"431300","townId":"431301","job":"bj"}
        try{
            if (user.getSex().equals("男")) {
                user.setSex("1");
            }else{
                user.setSex("2");
            }
            return userService.update(user);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }



}
