package com.pinyougou.cart.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-20<p>
 */
@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService{

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加SKU商品到购物车
     * @param carts 购物车集合
     * @param itemId SKU的id
     * @param num 购买数量
     * @return 修改后的购物车
     */
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num){
        try{
            // 1. 根据itemId从tb_item表查询一条数据(SKU商品)
            Item item = itemMapper.selectByPrimaryKey(itemId);

            // 获取商家id
            String sellerId = item.getSellerId();

            // 2. 根据商家id得到对应商家的购物车
            Cart cart = searchCartBySellerId(carts, sellerId);

            // 3. 判断cart(该用户是否购买过该商家商品)
            if (cart == null){
                // 没有购买过该商家的商品
                // 创建一个新购物车
                cart = new Cart();
                // 设置商家id
                cart.setSellerId(sellerId);
                // 设置商家名称
                cart.setSellerName(item.getSeller());
                // 创建购物车列表
                List<OrderItem> orderItems = new ArrayList<>();
                // 创建订单明细商品
                OrderItem orderItem = createOrderItem(item, num);
                // 往购物车列表中添加商品
                orderItems.add(orderItem);

                // 设置购物车列表
                cart.setOrderItems(orderItems);

                // 把商家的购物车添加到用户的购物车集合中
                carts.add(cart);
            }else{
                // 购买过该商家的商品
                // 获取商家的购物车列表
                List<OrderItem> orderItems = cart.getOrderItems();
                // 判断是否购买过同样的商品(根据itemId到用户的购物车列表中查询)
                OrderItem orderItem = searchOrderItemByItemId(orderItems, itemId);
                if (orderItem == null){ // 没有买过相同的商品
                    // 创建订单明细商品
                    orderItem = createOrderItem(item, num);
                    orderItems.add(orderItem);
                }else{
                    // 买过相同的商品
                    // 购买数量相加
                    orderItem.setNum(orderItem.getNum() + num);
                    // 小计金额
                    orderItem.setTotalFee(new BigDecimal(
                            orderItem.getPrice().doubleValue() * orderItem.getNum()));
                    // 判断购买数量是否等0
                    if (orderItem.getNum() == 0){
                        // 从商家的购物车列表中删除该商品
                        orderItems.remove(orderItem);
                    }
                    if (orderItems.size() == 0){
                        // 从用户购物车集合中删除商家的购物车
                        carts.remove(cart);
                    }

                }
            }

            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据itemId到商家的购物车列表中查询订单明细 */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems){
            if (itemId.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }

    /** 创建订单明细商品 */
    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        // 设置SKU的id
        orderItem.setItemId(item.getId());
        // 设置SPU的id
        orderItem.setGoodsId(item.getGoodsId());
        // 设置商品的标题
        orderItem.setTitle(item.getTitle());
        // 设置商品的价格
        orderItem.setPrice(item.getPrice());
        // 设置购买数量
        orderItem.setNum(num);
        // 设置小计金额
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        // 设置商品图片
        orderItem.setPicPath(item.getImage());
        // 设置商家的id
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    // 根据商家id得到对应商家的购物车
    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        // 迭代购物车集合
        for (Cart cart : carts){
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /** 购物车保存到Redis数据库 */
    public void saveCartRedis(String userId, List<Cart> carts){
        try{
            redisTemplate.boundValueOps("cart_" + userId).set(carts);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 从Redis数据库获取购物车 */
    public List<Cart> findCartRedis(String userId){
        try{
            List<Cart> carts = (List<Cart>)redisTemplate
                    .boundValueOps("cart_" + userId).get();
            if (carts == null){
                carts = new ArrayList<>();
            }
            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 购物车合并 */
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts){
        try{
            for (Cart cart : cookieCarts){
                for(OrderItem orderItem : cart.getOrderItems()){
                    redisCarts = addItemToCart(redisCarts, orderItem.getItemId(), orderItem.getNum());
                }
            }
            return redisCarts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
