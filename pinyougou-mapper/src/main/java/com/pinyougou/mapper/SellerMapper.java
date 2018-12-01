package com.pinyougou.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pinyougou.pojo.User;


import com.pinyougou.pojo.User;


import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

/**
 * SellerMapper 数据访问接口
 * @date 2018-10-31 15:50:48
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{




    /** 从数据库查询原密码进  行比较 */
    @Select("select password from tb_seller where seller_id = #{sellerId}")
    String findPassword(String sellerId);
    /** 更新密码   */
    @Update("update tb_seller set password = #{newPasswordE} where seller_id = #{sellerId}")
    void updatePassword(@Param("newPasswordE") String newPasswordE, @Param("sellerId") String sellerId);

    //查看用户
    Seller getPasswordBySellerId(String username);
    //保存信息
     void updateBymessage(Seller seller);




}