package com.pinyougou.service;

import com.pinyougou.pojo.SeckillOrder;
import java.util.List;
import java.io.Serializable;
/**
 * SeckillOrderService 服务接口
 * @date 2018-10-31 15:53:42
 * @version 1.0
 */
public interface SeckillOrderService {

	/** 添加方法 */
	void save(SeckillOrder seckillOrder);

	/** 修改方法 */
	void update(SeckillOrder seckillOrder);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	SeckillOrder findOne(Serializable id);

	/** 查询全部 */
	List<SeckillOrder> findAll();

	/** 多条件分页查询 */
	List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows);

	/** 把秒杀订单存储到Redis数据库 */
    void submitOrderToRedis(Long id, String userId);

    /** 根据用户id从Redis数据库查询秒杀订单 */
	SeckillOrder findSeckillOrderFromRedis(String userId);

	/** 保存秒杀订单 */
	void saveOrder(String userId, String transactionId);

	/** 查询所有超时未支付的订单 */
    List<SeckillOrder> findOrderByTimeout();

    /** 删除Redis数据库中的秒杀订单，增加库存 */
	void deleteOrderFromRedis(SeckillOrder seckillOrder);
}