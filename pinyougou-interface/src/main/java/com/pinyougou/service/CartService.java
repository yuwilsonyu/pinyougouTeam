package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List; /**
 * 购物车服务接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-20<p>
 */
public interface CartService {

    /**
     * 添加SKU商品到购物车
     * @param carts 购物车集合
     * @param itemId SKU的id
     * @param num 购买数量
     * @return 修改后的购物车
     */
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    /** 购物车保存到Redis数据库 */
    void saveCartRedis(String userId, List<Cart> carts);

    /** 从Redis数据库获取购物车 */
    List<Cart> findCartRedis(String userId);

    /** 购物车合并 */
    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts);
}
