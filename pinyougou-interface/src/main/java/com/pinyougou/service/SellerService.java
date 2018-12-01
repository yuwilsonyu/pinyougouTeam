package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import java.util.List;
import java.io.Serializable;
/**
 * SellerService 服务接口
 * @date 2018-10-30 16:10:23
 * @version 1.0
 */
public interface SellerService {

	/** 添加方法 */
	void save(Seller seller);

	/** 修改方法 */
	void update(Seller seller);

	/** 根据主键id删除   */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Seller findOne(Serializable id);

	/** 查询全部 */
	List<Seller> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Seller seller, int page, int rows);

	void updatePassword(String newPassword, String sellerId);

	String findPassword(String sellerId);
	/** 修改商家状态 */
	void updateStatus(String sellerId, String status);
	/**查看用户密码**/
	Seller checkPassword(String username);
	/**更新用户密码**/
	boolean updateSellerPasword(String username, String newPassword);


}