package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.Item;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * GoodsService 服务接口
 * @date 2018-10-31 15:53:42
 * @version 1.0
 */
public interface GoodsService {

	/** 添加方法 */
	void save(Goods goods);

	/** 修改方法 */
	void update(Goods goods);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Long[] ids);

	/** 根据主键id查询 */
	Goods findOne(Serializable id);

	/** 查询全部 */
	List<Goods> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Goods goods, int page, int rows);

	/** 商品审核(修改商品状态) */
    void updateStatus(Long[] ids, String status);

    /**  修改商品上下架状态 */
    void updateMarketable(Long[] ids, String status);

    /** 根据SPU的id查询商品信息 */
    Map<String,Object> getGoods(Long goodsId);

    /** 根据goodsIds查询上架通过的SKU商品 */
    List<Item> findItemByGoodsId(Long[] goodsIds);
}