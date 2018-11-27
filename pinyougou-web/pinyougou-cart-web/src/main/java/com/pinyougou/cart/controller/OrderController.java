package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-23<p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /** 保存订单 */
    @PostMapping("/saveOrder")
    public boolean saveOrder(@RequestBody Order order, HttpServletRequest request){
        try{
            // 获取登录用户名
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            orderService.save(order);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 生成微信支付二维码
     */
    @GetMapping("/genPayCode")
    public Map<String,Object> genPayCode(HttpServletRequest request){
        // {outTradeNo : '', totalFee : '', codeUrl : ''}
        // 获取登录用户名
        String userId = request.getRemoteUser();
        // 根据用户id从Redis数据库查询支付日志
        PayLog payLog = orderService.findPayLogByUser(userId);

        return weixinPayService.genPayCode(payLog.getOutTradeNo(),
                String.valueOf(payLog.getTotalFee()));
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
                    orderService.updatePayStatus(outTradeNo, map.get("transaction_id"));

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
