package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.User;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * UserService 服务接口
 *
 * @version 1.0
 * @date 2018-10-31 15:53:42
 */
public interface UserService {

    /**
     * 添加方法
     */
    void save(User user);

    /**
     * 修改方法
     */
    boolean update(User user);

    /**
     * 根据主键id删除
     */
    void delete(Serializable id);

    /**
     * 批量删除
     */
    void deleteAll(Serializable[] ids);

    /**
     * 根据主键id查询
     */
    User findOne(Serializable id);

    /**
     * 查询全部
     */
    List<User> findAll();

    /**
     * 多条件分页查询
     */
    List<User> findByPage(User user, int page, int rows);

    /**
     * 发送短信验证码
     */
    boolean sendSmsCode(String phone);

    /**
     * 检验短信验证码
     */
    boolean checkSmsCode(String smsCode, String phone);

    Map<String, Object> selectOneByUserName(String userName);
	/** 检验短信验证码 */
	boolean checkSmsCode(String smsCode, String phone);

	/** 更新用户密码*/
    boolean updateUserpassword(String username, String password);

    /** 获取用户手机号*/
	String getUserPhone(String username);

	/** 更新用户手机号*/
    boolean updateUserPhone(String username, String newPhone);

	/** 获取用户订单列表*/
	PageResult getOrdersByPage(Order order, Integer page , Integer rows);
	/**  修改支付状态 */
	void updatePayStatus(String outTradeNo);
}