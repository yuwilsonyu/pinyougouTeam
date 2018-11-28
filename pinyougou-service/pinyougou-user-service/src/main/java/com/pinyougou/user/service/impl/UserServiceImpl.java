package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-17<p>
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(User user) {
        try{
            // 密码加密(MD5) commons-codec.xx.jar
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            // 创建时间
            user.setCreated(new Date());
            // 修改时间
            user.setUpdated(user.getCreated());
            // 添加
            userMapper.insertSelective(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean update(User user) {
        try{
            userMapper.update(user);
            return true;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /** 发送短信验证码 */
    public boolean sendSmsCode(String phone){
        try{
            // 1. 随机生成6位数字的验证码
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-zA-Z]", "").substring(0, 6);
            System.out.println("验证码：" + code);
            // 2. 调用短信发送接口
            // 调用短信接口
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            /**
             * phone      String  必须  待发送手机号
              signName  String  必须  短信签名-可在短信控制台中找到
              templateCode  String 必须  短信模板-可在短信控制台中找到
              templateParam String 必须 模板中的变量替换 JSON 串,如模板内容为"亲爱的${name},
              您的验证码为${code}"时,此处的值为{"name" : "", "code" : ""}
              15219518251
             */
            // 定义Map集合封装请求参数
            Map<String, String> params = new HashMap<>();
            params.put("phone", phone);
            params.put("signName", signName);
            params.put("templateCode", templateCode);
            params.put("templateParam", "{'number' : '"+ code +"'}");

            // 执行post请求 {}
            String content = httpClientUtils.sendPost(smsUrl, params);
            // 把json字符串转化成Map集合
            Map<String,Object> map = JSON.parseObject(content, Map.class);
            // 判断是否发送成功
            boolean success = (boolean)map.get("success");
            if (success) {
                // 3. 把验证码存储到Redis数据库(有效时间 90秒)
                redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            }
            return success;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 检验短信验证码 */
    public boolean checkSmsCode(String smsCode, String phone){
        try{
            // 从Redis数据库中获取验证码
            String code = (String)redisTemplate.boundValueOps(phone).get();

            return code != null && code.equals(smsCode);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
