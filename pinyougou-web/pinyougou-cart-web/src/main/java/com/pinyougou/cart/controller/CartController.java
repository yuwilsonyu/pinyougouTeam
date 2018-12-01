package com.pinyougou.cart.controller;
import com.pinyougou.pojo.OrderItem;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartCountService;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 购物车控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-20<p>
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference(timeout = 100000)
    private CartService cartService;
    @Reference(timeout = 100000)
    private CartCountService cartCountService;

    /** 添加SKU商品到购物车 */
    @GetMapping("/addCart")
    @CrossOrigin(origins = {"http://item.pinyougou.com"},
            allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num){
        try{
            // 添加响应头(允许哪个源可以访问) 99%
            // response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
            // 设置允许访问Cookie
            // response.setHeader("Access-Control-Allow-Credentials", "true");

            // 获取登录用户名
            String userId = request.getRemoteUser();
            /** ####### 获取原来的购物车数据 ######## */
            List<Cart> carts = findCart();
            // 往购物车中添加商品，返回修改后的购物车
            carts = cartService.addItemToCart(carts, itemId, num);

            // 判断用户是否登录
            if (StringUtils.isNoneBlank(userId)) { // 已登录
                /** ######### 已登录的用户，把购物车存储到Redis数据库 ############ */
                cartService.saveCartRedis(userId, carts);

            }else{ // 未登录
                /** ######### 未登录的用户，把购物车存储到Cookie中 ############ */
                // List<Cart> 转化成 json字符串 [{},{}]
                CookieUtils.setCookie(request, response,
                        CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(carts),
                        3600 * 24, true);
            }

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 获取购物车数据 */
    @GetMapping("/findCart")
    public List<Cart> findCart(){

        // 获取登录用户名
        String userId = request.getRemoteUser();
        // 购物车集合
        List<Cart> carts = null;

        // 判断用户是否登录
        if (StringUtils.isNoneBlank(userId)){ // 已登录
            /**######## 已登录的用户，从Redis中获取购物车数据 ###########*/
            carts = cartService.findCartRedis(userId);


            /** ###### 把Cookie中的购物车数据合并到Redis中 ####### */
            // 获取Cookie中的购物车数据
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isNoneBlank(cartStr)){ // 不是空
                // 把json字符串转化成List集合
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                if (cookieCarts != null && cookieCarts.size() > 0){
                    // 返回合并后得购物车集合
                    carts = cartService.mergeCart(cookieCarts, carts);
                    // 存储到Redis数据库
                    cartService.saveCartRedis(userId, carts);
                    // 删除Cookie中的购物车
                    CookieUtils.deleteCookie(request,response,
                            CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }


        }else{ // 未登录
            /**######## 未登录的用户，从Cookie中获取购物车数据 ###########*/
            // List<Cart> 转化成 json字符串 [{},{}]
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            // 判断购物车json字符串是否为空
            if (StringUtils.isBlank(cartStr)){
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;
    }



    /** 添加SKU商品到购物车只存在cookie中 */
    public List<Cart> addCookieCart(Long itemId, Integer num){
        try{
            // 获取购物车集合
            List<Cart> carts = findCookieCart();
            // 调用服务层添加SKU商品到购物车
            carts = cartCountService.addItemToCartCookie(carts, itemId, num);
            return carts;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    /** 查询购物车集合只存在cookie中 */
    @GetMapping("/findCookieCart")
    public List<Cart> findCookieCart() {
        // 从Cookie中获取购物车集合json字符串
        String cartStr = CookieUtils.getCookieValue(request,
                CookieUtils.CookieName.PINYOUGOUCOUNT_CART, true);
        // 判断是否为空
        if (StringUtils.isBlank(cartStr)){
            cartStr = "[]";
        }
        List<Cart> carts = JSON.parseArray(cartStr, Cart.class);
        return carts;
    }


    /**用户勾选后添加购物车集合只存在cookie中 */
    @GetMapping("/addCountCart")
    public boolean addCountCart(Long[] itemIds, Integer[] nums){
        if (itemIds!=null&&nums!=null) {
            List<Cart> newCarts=new ArrayList<>();
            for (int i=0;i<itemIds.length;i++) {
                Long itemId = itemIds[i];
                Integer num = nums[i];
                //更新原始购物车
                addCart(itemId,-num);


                List<Cart> carts = addCookieCart(itemId, num);
                for (Cart cart : carts) {
                    newCarts.add(cart);
                }
            }
            // 将结算购物车重新存入Cookie中
            CookieUtils.setCookie(request, response,
                    CookieUtils.CookieName.PINYOUGOUCOUNT_CART,
                    JSON.toJSONString(newCarts),
                    180, true);
            return true;
        } else {
            return false;
        }
    }

}
