package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.util.List;
import java.util.Map;

/**
 * BrandMapper 数据访问接口
 * @date 2018-10-31 15:50:48
 * @version 1.0
 */
public interface BrandMapper extends Mapper<Brand>{

    /** 多条件查询品牌 */
    List<Brand> findAll(Brand brand);

    /**  查询全部品牌(id与name) */
    @Select("select id, name as text from tb_brand order by id asc")
    List<Map<String,Object>> findAllByIdAndName();
}