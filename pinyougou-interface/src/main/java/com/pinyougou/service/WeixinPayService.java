package com.pinyougou.service;

import java.util.Map;

/**
 * 微信支付服务接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-24<p>
 */
public interface WeixinPayService {

    /**
     * 调用微信支付系统中的“统一下单”接口，
     * 获取支付URL
     */
    Map<String,Object> genPayCode(String outTradeNo, String totalFee);

    /**
     * 调用微信支付系统中的“查询订单”接口,
     * 获取支付状态
     */
    Map<String,String> queryPayStatus(String outTradeNo);

    /**
     * 调用微信支付系统中的"关闭订单" 接口，
     * 获取关单状态
     */
    Map<String,String> closePayTimeout(String outTradeNo);
}
