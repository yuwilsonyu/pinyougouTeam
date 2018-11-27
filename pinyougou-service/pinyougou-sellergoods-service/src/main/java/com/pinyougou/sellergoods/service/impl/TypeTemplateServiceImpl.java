package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 类型模板服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-02<p>
 */
@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional(rollbackFor = RuntimeException.class)
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(TypeTemplate typeTemplate) {
        try{
            // 选择性添加，会判断对象的属性是否有值(有值就生成到insert语句中)
            typeTemplateMapper.insertSelective(typeTemplate);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        try{
            // 选择性修改，会判断对象的属性是否有值(有值就生成到update语句中)
            typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
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
            // 创建示范对象
            Example example = new Example(TypeTemplate.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // in条件
            criteria.andIn("id", Arrays.asList(ids));
            // 条件删除
            typeTemplateMapper.deleteByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        try{
            return typeTemplateMapper.selectByPrimaryKey(id);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try{
            // 开始分页
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    // 多条件查询
                    // 创建示范对象
                    Example example = new Example(TypeTemplate.class);
                    // 创建条件对象
                    Example.Criteria criteria = example.createCriteria();
                    // 判断模板名称
                    if (typeTemplate != null && !StringUtils.isEmpty(typeTemplate.getName())) {
                        // name like ?
                        criteria.andLike("name", "%" + typeTemplate.getName() + "%");
                    }
                    // 条件查询
                    typeTemplateMapper.selectByExample(example);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据类型模板id查询规格选项 */
    public List<Map> findSpecByTemplateId(Long id){
        try{
            // 根据id查询类型模板对象
            TypeTemplate typeTemplate = findOne(id);
            // 获取关联的规格spec_ids
            // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            String specIds = typeTemplate.getSpecIds();
            // 把json字符串转化成List<Map>集合
            List<Map> mapList = JSON.parseArray(specIds, Map.class);
            // 循环集合
            for (Map map : mapList){
                // map: {"id":27,"text":"网络"}
                // 获取id
                Integer specId = (Integer) map.get("id");
                // select * from tb_specification_option where spec_id = 27
                SpecificationOption so = new SpecificationOption();
                so.setSpecId(specId.longValue());
                List<SpecificationOption> soList = specificationOptionMapper.select(so);

                // {"id":27,"text":"网络", "options" : [{},{}]}
                // 往map对象中添加一个key
                map.put("options", soList);
            }
            return mapList;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
