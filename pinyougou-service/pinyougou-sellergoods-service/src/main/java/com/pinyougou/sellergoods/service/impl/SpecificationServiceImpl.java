package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.Specification;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 规格服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-01<p>
 */
@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(Specification specification) {
        try{
            // 往tb_specification规格表插入数据
            specificationMapper.insertSelective(specification);

            // 往tb_specification_option规格选项表插入数据(多条数据)
            specificationOptionMapper.save(specification);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Specification specification) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Specification findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Specification> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Specification specification, int page, int rows) {
        try{
            // 开启分页
            PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    // 条件查询
                    specificationMapper.findAll(specification);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询全部规格(id与specName) */
    public List<Map<String, Object>> findAllByIdAndName(){
        try{
            return specificationMapper.findAllByIdAndName();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
