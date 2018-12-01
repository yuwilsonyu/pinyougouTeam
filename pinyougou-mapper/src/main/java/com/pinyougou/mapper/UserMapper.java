package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

import java.util.List;

/**
 * UserMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-10-31 15:50:48
 */
public interface UserMapper extends Mapper<User> {
    /**
     * 更新用户信息
     */
    void update(@Param("user") User user);

    /**
     * 根据username用户查询
     */
    User selectOneByUserName(@Param("userName") String userName);


    String getUserPhone(String username);
}