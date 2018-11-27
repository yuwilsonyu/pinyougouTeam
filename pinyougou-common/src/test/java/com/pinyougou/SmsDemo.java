package com.pinyougou;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsDemo
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-17<p>
 */
public class SmsDemo {

    public static void main(String[] args) {
        // 调用短信接口
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        /**
         * phone      String  必须  待发送手机号
           signName  String  必须  短信签名-可在短信控制台中找到
           templateCode  String 必须  短信模板-可在短信控制台中找到
           templateParam String 必须 模板中的变量替换 JSON 串,如模板内容为"亲爱的${name},
                   您的验证码为${code}"时,此处的值为{"name" : "", "code" : ""}
         */
        // 定义Map集合封装请求参数
        Map<String, String> params = new HashMap<>();
        params.put("phone", "15219518251");
        params.put("signName", "五子连珠");
        params.put("templateCode", "SMS_11480310");
        params.put("templateParam", "{'number' : '809009'}");

        // 执行post请求
        String content = httpClientUtils
                .sendPost("http://sms.pinyougou.com/sms/sendSms", params);
        System.out.println(content);
    }
}
