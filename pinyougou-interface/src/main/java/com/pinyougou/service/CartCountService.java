package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

/** 已选购物车服务接口 */
public interface CartCountService {
    /**
     * 添加SKU商品到购物车
     * @param carts 购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num 购买数据
     * @return 修改后的购物车
     */
    List<Cart> addItemToCartCookie(List<Cart> carts, Long itemId, Integer num);
}
