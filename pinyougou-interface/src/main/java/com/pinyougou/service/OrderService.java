package com.pinyougou.service;

import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;

import java.util.List;
import java.io.Serializable;
/**
 * OrderService 服务接口
 * @date 2018-10-31 15:53:42
 * @version 1.0
 */
public interface OrderService {

	/** 添加方法 */
	void save(Order order);

	/** 修改方法 */
	void update(Order order);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Order findOne(Serializable id);

	/** 查询全部 */
	List<Order> findAll();

	/** 多条件分页查询 */
	List<Order> findByPage(Order order, int page, int rows);

	/** 根据用户id从Redis数据库查询支付日志 */
    PayLog findPayLogByUser(String userId);

	/**  修改支付状态 */
	void updatePayStatus(String outTradeNo, String transactionId);
}