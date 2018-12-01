package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import com.pinyougou.service.WeixinPayService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
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

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

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

    /**
     * 发送短信验证码到用户手机
     */
    @GetMapping("/sendCode")
    public boolean sendCode(String phone) {
        try {
            return userService.sendSmsCode(phone);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    public boolean update(@RequestBody User user) {
//        {"headPic":"xxxx","nickName":"abab","sex":"男","birthday":"2018-10-31","provinceId":"430000","cityId":"431300","townId":"431301","job":"bj"}
        try {
            userService.update(user);
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 根据username用户查询
     */
    @GetMapping("/selectOneByUserName")
    public Map<String, Object> selectOneByUserName(@RequestParam("userName") String userName) {
        try {
            return userService.selectOneByUserName(userName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
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

    /** 获取用户订单列表*/
    @GetMapping("/getOrdersByPage")
    public PageResult getOrdersByPage(Integer page ,Integer rows){
        String userId = httpServletRequest.getRemoteUser();
        Order order = new Order();
        order.setUserId(userId);
        return userService.getOrdersByPage(order,page,rows);
    }

    /**生成微信支付二维码url*/
    @GetMapping("/genPayCode")
    public Map<String,Object> genPayCode(String outTradeNo, String totalFee){
        return weixinPayService.genPayCode(outTradeNo,totalFee);
    }

    /**
     * 检测支付状态
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo){
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            // 调用支付服务
            Map<String,String> map = weixinPayService.queryPayStatus(outTradeNo);
            // 判断交易状态码
            if (map != null && map.size() > 0){
                // 支付成功
                if ("SUCCESS".equals(map.get("trade_state"))){
                    // 修改支付状态
                    userService.updatePayStatus(outTradeNo);
                    data.put("status", 1);
                }
                // NOTPAY—未支付
                if ("NOTPAY".equals(map.get("trade_state"))){
                    data.put("status", 2);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
