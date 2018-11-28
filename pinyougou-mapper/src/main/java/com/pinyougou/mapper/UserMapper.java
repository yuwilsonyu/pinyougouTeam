package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

/**
 * UserMapper 数据访问接口
 * @date 2018-10-31 15:50:48
 * @version 1.0
 */
public interface UserMapper extends Mapper<User>{


    void update(@Param("user")User user);
}