package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-24<p>
 */
@Service(interfaceName = "com.pinyougou.service.WeixinPayService")
public class WeixinPayServiceImpl implements WeixinPayService {


    /** 微信公众账号或开放平台APP的唯一标识 */
    @Value("${appid}")
    private String appid;
    /** 商户账号 */
    @Value("${partner}")
    private String partner;
    /** 商户密钥 */
    @Value("${partnerkey}")
    private String partnerkey;
    /** 统一下单接口URL */
    @Value("${unifiedorder}")
    private String unifiedorder;
    /** 查询订单接口URL */
    @Value("${orderquery}")
    private String orderquery;
    /** 关闭订单接口URL */
    @Value("${closeorder}")
    private String closeorder;


    /**
     * 调用微信支付系统中的“统一下单”接口，
     * 获取支付URL*
     */
    public Map<String,Object> genPayCode(String outTradeNo, String totalFee){
        try{
            // 1. 封装请求参数(参考api文档)
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            // 商品描述	body
            params.put("body", "品优购");
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 订单总金额，单位为分
            params.put("total_fee", totalFee);
            // 终端IP	spbill_create_ip
            params.put("spbill_create_ip", "127.0.0.1");
            // 通知地址	notify_url
            params.put("notify_url", "http://www.pinyougou.com");
            // 交易类型	trade_type  (Native支付)
            params.put("trade_type", "NATIVE");
            // 对请求参数加"签名"	sign
            String paramXml = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数：" + paramXml);


            // 2. 调用“统一下单接口”
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String dataXml = httpClientUtils.sendPost(unifiedorder, paramXml);
            System.out.println("响应数据：" + dataXml);


            // 3. 处理响应数据
            // 把xml格式的响应数据，转化成Map集合
            Map<String, String> map = WXPayUtil.xmlToMap(dataXml);

            // 创建Map集合封装响应数据
            Map<String, Object> data= new HashMap<>();
            data.put("outTradeNo", outTradeNo);
            data.put("totalFee", totalFee);
            // 二维码链接	code_url
            data.put("codeUrl", map.get("code_url"));
            return data;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 调用微信支付系统中的“查询订单”接口,
     * 获取支付状态
     */
    public Map<String,String> queryPayStatus(String outTradeNo){
        try{
            // 1. 封装请求参数(参考api文档)
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());

            // 对请求参数加"签名"	sign
            String paramXml = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数：" + paramXml);


            // 2. 调用“查询订单接口”
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String dataXml = httpClientUtils.sendPost(orderquery, paramXml);
            System.out.println("响应数据：" + dataXml);

            // 3. 处理响应数据
            // 把xml格式的响应数据，转化成Map集合
            return WXPayUtil.xmlToMap(dataXml);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 调用微信支付系统中的"关闭订单" 接口，
     * 获取关单状态
     */
    public Map<String,String> closePayTimeout(String outTradeNo){
        try{
            // 1. 封装请求参数(参考api文档)
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());

            // 对请求参数加"签名"	sign
            String paramXml = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数：" + paramXml);


            // 2. 调用“关闭订单接口”
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String dataXml = httpClientUtils.sendPost(closeorder, paramXml);
            System.out.println("响应数据：" + dataXml);

            // 3. 处理响应数据
            // 把xml格式的响应数据，转化成Map集合
            return WXPayUtil.xmlToMap(dataXml);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
