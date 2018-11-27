package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

/**
 * 品牌服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-10-29<p>
 */
@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {
    /** 注入数据访问接口代理对象 */
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public void save(Brand brand) {
        try{
            brandMapper.insertSelective(brand);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Brand brand) {
        try{
            brandMapper.updateByPrimaryKeySelective(brand);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try{

            //  DELETE FROM tb_brand WHERE ( id in ( ? , ? , ? ) )
            // 创建示范对象
            Example example = new Example(Brand.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 封装删除条件 id in(?,?)
            criteria.andIn("id", Arrays.asList(ids));
            // 条件删除
            brandMapper.deleteByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Brand findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Brand> findAll() {
       try{
           return brandMapper.selectAll();
       }catch (Exception ex){
           throw new RuntimeException(ex);
       }
    }

    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        try{
            // 开启分页查询
            PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    brandMapper.findAll(brand);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询全部品牌(id与name)  */
    public List<Map<String,Object>> findAllByIdAndName(){
        try{
            // [{id : 1, text : '华为'},{id : 2, text : '小米'}]
            return brandMapper.findAllByIdAndName();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
