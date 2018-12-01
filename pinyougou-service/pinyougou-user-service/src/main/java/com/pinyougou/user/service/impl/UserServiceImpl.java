package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
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
        try {
            // 密码加密(MD5) commons-codec.xx.jar
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            // 创建时间
            user.setCreated(new Date());
            // 修改时间
            user.setUpdated(user.getCreated());
            // 添加
            userMapper.insertSelective(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean update(User user) {
        try {
            //时间装换
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getBirthday());//将时间格式转换成符合Timestamp要求的格式.
            java.sql.Timestamp newBirthday = java.sql.Timestamp.valueOf(nowTime);
            user.setBirthday(newBirthday);
            userMapper.update(user);
            return true;
        } catch (Exception ex) {
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

    /**
     * 发送短信验证码
     */
    public boolean sendSmsCode(String phone) {
        try {
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
            params.put("templateParam", "{'number' : '" + code + "'}");

            // 执行post请求 {}
            String content = httpClientUtils.sendPost(smsUrl, params);
            // 把json字符串转化成Map集合
            Map<String, Object> map = JSON.parseObject(content, Map.class);
            // 判断是否发送成功
            boolean success = (boolean) map.get("success");
            if (success) {
                // 3. 把验证码存储到Redis数据库(有效时间 90秒)
                redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            }
            return success;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /** 获取用户手机号*/
    @Override
    public String getUserPhone(String username) {
        try{
            return userMapper.getUserPhone(username);
        }catch (Exception ex){
         throw new RuntimeException(ex);
        }
    }

    /**  修改支付状态 */
    @Override
    public void updatePayStatus(String outTradeNo) {
        try{
            Example example = new Example(Order.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId",outTradeNo);
            Order order = new Order();
            order.setPaymentTime(new Date());
            order.setUpdateTime(new Date());
            order.setPaymentType("1");
            order.setStatus("2");
            orderMapper.updateByExampleSelective(order,example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**分页获取用户订单列表*/
    @Override
    public PageResult getOrdersByPage(Order order, Integer page , Integer rows) {
        try {
            PageInfo<Order> pageInfo = PageHelper.startPage(page,rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    orderMapper.select(order);
                }
            });
            List<Order> orderList = pageInfo.getList();
            if (orderList != null &&orderList.size() >0){
                for (Order order1 : orderList) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order1.getOrderId());
                    order1.setOrderItemList(orderItemMapper.select(orderItem));//获取订单商品清单,并添加到订单商品清单集合
                }
            }
            return new PageResult(pageInfo.getTotal(),orderList);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 更新用户手机*/
    @Override
    public boolean updateUserPhone(String username, String newPhone) {
        try{
            User user= new User();
            user.setPhone(newPhone);
            user.setUpdated(new Date());
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username",username);
            userMapper.updateByExampleSelective(user,example);
            return true;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 更新用户密码 */
    @Override
    public boolean updateUserpassword(String username, String password) {
        try{
            User user = new User();
            user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex(password));
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username",username);
            userMapper.updateByExampleSelective(user,example);
            return true;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 检验短信验证码
     */
    public boolean checkSmsCode(String smsCode, String phone) {
        try {
            // 从Redis数据库中获取验证码
            String code = (String) redisTemplate.boundValueOps(phone).get();

            return code != null && code.equals(smsCode);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据username用户查询
     */
    public Map<String, Object> selectOneByUserName(String userName) {
        try {
            Map<String, Object> map = new HashMap<>();
            User user = userMapper.selectOneByUserName(userName);
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getBirthday());//将时间格式转换成符合Timestamp要求的格式.
            map.put("userName", user.getUsername());
            map.put("nickName", user.getNickName());
            map.put("headPic", user.getHeadPic());
            map.put("sex", user.getSex());
            map.put("job", user.getJob());
            map.put("birthday", nowTime);
            map.put("address", user.getAddress());
            return map;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
