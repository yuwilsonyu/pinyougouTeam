package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-23<p>
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PayLogMapper payLogMapper;

    /** 保存订单 */
    @Override
    public void save(Order order) {
        try{
            // 从Redis数据库中获取当前用户的购物车集合
            List<Cart> carts = (List<Cart>) redisTemplate
                    .boundValueOps("cart_" + order.getUserId()).get();

            // 定义支付总金额
            double totalMoney = 0;
            // 定义多个订单字符串
            String orderIds = "";

            /** ######## 往订单表插入数据 ######### */
            // 一个商家(Cart)产生一个订单
            for (Cart cart : carts){
                // 创建订单
                Order order1 = new Order();
                // 生成主键id
                long orderId = idWorker.nextId();
                // 订单id
                order1.setOrderId(orderId);
                // 支付方式
                order1.setPaymentType(order.getPaymentType());
                // 支付状态 1、未付款
                order1.setStatus("1");
                // 创建时间
                order1.setCreateTime(new Date());
                // 修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 用户id
                order1.setUserId(order.getUserId());
                // 设置收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());


                // 定义订单的总金额
                double money = 0;

                /** ######## 往订单明细表插入数据 ######### */
                for (OrderItem orderItem : cart.getOrderItems()){
                    // 主键id
                    orderItem.setId(idWorker.nextId());
                    // 订单id
                    orderItem.setOrderId(orderId);

                    // 计算出该订单的总金额
                    money += orderItem.getTotalFee().doubleValue();

                    // 插入数据到tb_order_item
                    orderItemMapper.insertSelective(orderItem);
                }


                // 支付的总金额
                order1.setPayment(new BigDecimal(money));
                // 插入数据到tb_order
                orderMapper.insertSelective(order1);

                // 计算支付总金额(多个订单的金额相加)
                totalMoney += money;
                // 拼接多个订单
                orderIds +=  orderId + ",";
            }

            /**################### 多个订单组成一次支付(购物车中的商品一次性支付) #################### */
            // 判断是在线支付
            if ("1".equals(order.getPaymentType())){
                // 生成支付日志
                PayLog payLog = new PayLog();
                payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
                // 创建时间
                payLog.setCreateTime(new Date());
                // 支付总金额 (分)
                long totalFee = (long)(totalMoney * 100);
                payLog.setTotalFee(totalFee);
                // 付款的用户
                payLog.setUserId(order.getUserId());
                // 交易状态(未支付)
                payLog.setTradeState("0");
                // 多个订单id
                payLog.setOrderList(orderIds.substring(0,orderIds.length() - 1));
                // 支付类型
                payLog.setPayType(order.getPaymentType());
                // 往支付日志表中插入数据
                payLogMapper.insertSelective(payLog);

                // 把支付日志对象存入Redis数据库
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
            }


            // 从Redis数据库删除该用户的购物车
            redisTemplate.delete("cart_" + order.getUserId());

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }

    /** 根据用户id从Redis数据库查询支付日志 */
    public PayLog findPayLogByUser(String userId){
        try{
            return (PayLog)redisTemplate.boundValueOps("payLog_" + userId).get();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**  修改支付状态 */
    public void updatePayStatus(String outTradeNo, String transactionId){
        try{
            //1. 修改支付日志表
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            // 微信订单号
            payLog.setTransactionId(transactionId);
            // 支付时间
            payLog.setPayTime(new Date());
            // 支付状态(支付成功)
            payLog.setTradeState("1");
            // 修改
            payLogMapper.updateByPrimaryKeySelective(payLog);

            //2. 修改订单状态
            String[] orderIds = payLog.getOrderList().split(",");
            for (String orderId : orderIds){
                Order order1 = new Order();
                // 主键id
                order1.setOrderId(Long.valueOf(orderId));
                // 已付款
                order1.setStatus("2");
                // 支付时间
                order1.setPaymentTime(new Date());
                // 修改订单表
                orderMapper.updateByPrimaryKeySelective(order1);
            }

            //3. 从Redis中删除支付日志对象
            redisTemplate.delete("payLog_" + payLog.getUserId());

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
