package com.pinyougou.seckill.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 秒杀订单服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-26<p>
 */
@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    /**
     * 提交订单到Redis
     * synchronized : 线程锁(单个进程相关)
     * 分布式锁 (多个进程相关) Redis实现分布式锁
     * 多个进程都可以访问同一台Redis 1.txt
     * 高并发 10w/s  100个商品
     *
     * */
    @Override
    public synchronized void submitOrderToRedis(Long id, String userId) {
        try{
            // 从Redis数据库中获取该秒杀商品，判断剩余库存
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.
                    boundHashOps("seckillGoodsList").get(id);
            if (seckillGoods != null && seckillGoods.getStockCount() > 0){
                /** ######### 发送MQ消息完成秒杀下单(userId 、 id) ##########*/
                // 减库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 同步剩余库存数量到数据库 Redis(rdb、aof)

                // 判断库存数量
                if (seckillGoods.getStockCount() == 0){ // 秒光了
                    // 同步到数据库
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 从Redis数据库中删除该秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                }else{
                    // 把秒杀商品同步到Redis
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }

                // 产生秒杀订单
                SeckillOrder seckillOrder = new SeckillOrder();
                // 主键id
                seckillOrder.setId(idWorker.nextId());
                // 秒杀商品id
                seckillOrder.setSeckillId(id);
                // 金额
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 用户
                seckillOrder.setUserId(userId);
                // 商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 创建时间
                seckillOrder.setCreateTime(new Date());
                // 支付状态
                seckillOrder.setStatus("0");
                // 把秒杀预订单存储到Redis数据库
                redisTemplate.boundHashOps("seckillOrderList")
                        .put(userId, seckillOrder);
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据用户id从Redis数据库查询秒杀订单 */
    public synchronized SeckillOrder findSeckillOrderFromRedis(String userId){
        try{
            return (SeckillOrder)redisTemplate.boundHashOps("seckillOrderList").get(userId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 保存秒杀订单 */
    public void saveOrder(String userId, String transactionId){
        try{
            // 1. 从Redis数据库中获取秒杀订单
            SeckillOrder seckillOrder = findSeckillOrderFromRedis(userId);
            // 2. 同步到数据库表
            seckillOrder.setStatus("1");
            seckillOrder.setPayTime(new Date());
            seckillOrder.setTransactionId(transactionId);
            seckillOrderMapper.insertSelective(seckillOrder);
            // 3. 从Redis数据库中删除秒杀订单
            redisTemplate.boundHashOps("seckillOrderList").delete(userId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询所有超时未支付的订单 */
    public List<SeckillOrder> findOrderByTimeout(){
        try{
            // 定义集合封装超时未支付的定单
            List<SeckillOrder> seckillOrders = new ArrayList<>();

            // 1. 查询全部未支付的订单
            List<SeckillOrder> seckillOrderList = redisTemplate
                    .boundHashOps("seckillOrderList").values();
            // 2. 循环判断哪些秒杀订单超时
            for (SeckillOrder seckillOrder : seckillOrderList){
                // 获取订单的创建时间 与 当前系统时间进行比较
                // 获取当前时间毫秒数 - 5分钟的毫秒数
                long time = new Date().getTime() - 5 * 60 * 1000;
                // 判断是否为5分钟之前创建订单
                if (seckillOrder.getCreateTime().getTime() < time){
                    // 把超时秒杀订单添加到集合
                    seckillOrders.add(seckillOrder);
                }
            }
            // 3. 返回超时订单
            return seckillOrders;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 删除Redis数据库中的秒杀订单，增加库存 */
    public void deleteOrderFromRedis(SeckillOrder seckillOrder){
        try{
            // 1. 删除Redis数据库中的秒杀订单
            redisTemplate.boundHashOps("seckillOrderList")
                    .delete(seckillOrder.getUserId());
            // 2. 增加秒杀商品的库存
            // 从Redis数据中获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList")
                    .get(seckillOrder.getSeckillId());
            // 判断秒杀商品
            if (seckillGoods != null){
                // 增加库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }else{ // 0
                // 根据主键id从数据库表查询秒杀商品
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                seckillGoods.setStockCount(1);
            }
            // 同步到Redis数据库
            redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
