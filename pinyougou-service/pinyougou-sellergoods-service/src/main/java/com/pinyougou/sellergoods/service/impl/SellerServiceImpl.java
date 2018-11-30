package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商家服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-05<p>
 */
/*
@Service(interfaceName = "com.pinyougou.service.SellerService")
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    */
/** 商家申请入驻 *//*

    @Override
    public void save(Seller seller) {
        try{
            // 商家审核状态(未审核)
            seller.setStatus("0");
            // 创建时间
            seller.setCreateTime(new Date());
            sellerMapper.insertSelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Seller seller) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Seller findOne(Serializable id) {
        try{
            return sellerMapper.selectByPrimaryKey(id);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Seller> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Seller seller, int page, int rows) {
        try{
            // 开始分页
            PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    // 创建示范对象
                    Example example = new Example(Seller.class);
                    // 创建条件对象
                    Example.Criteria criteria = example.createCriteria();
                    // 状态码 status = ?
                    if (seller != null && !StringUtils.isEmpty(seller.getStatus())) {
                        criteria.andEqualTo("status", seller.getStatus());
                    }
                    // 公司名称 name like ?
                    if (seller != null && !StringUtils.isEmpty(seller.getName())){
                        criteria.andLike("name", "%"+ seller.getName() +"%");
                    }
                    // 店铺名称 nick_name like ?
                    if (seller != null && !StringUtils.isEmpty(seller.getNickName())){
                        criteria.andLike("nickName", "%"+ seller.getNickName() +"%");
                    }
                    sellerMapper.selectByExample(example);
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    */
/** 修改商家状态 *//*

    public void updateStatus(String sellerId, String status){
        try{
            // update tb_seller set status = ? where seller_id = ?
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setStatus(status);
            sellerMapper.updateByPrimaryKeySelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
*/


@Service(interfaceName = "com.pinyougou.service.SellerService")
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;


    /** 从数据库查询用户旧密码 */
    public String findPassword(String sellerId) {
        return sellerMapper.findPassword(sellerId);
    }
    /** 修改密码 */
    public void updatePassword(String newPassword, String sellerId) {
        try {
            sellerMapper.updatePassword(newPassword, sellerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加方法
     */
    public void save(Seller seller) {
        try {
            seller.setCreateTime(new Date());
            seller.setStatus("0");
            sellerMapper.insertSelective(seller);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(Seller seller) {
        try {
            sellerMapper.updateByPrimaryKeySelective(seller);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            sellerMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Seller.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            sellerMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Seller findOne(Serializable id) {
        try {
            return sellerMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Seller> findAll() {
        try {
            return sellerMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(Seller seller, int page, int rows) {
        try {
            PageInfo<Seller> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            // 创建示范对象
                            Example example = new Example(Seller.class);
                            // 创建条件对象
                            Example.Criteria criteria = example.createCriteria();
                            if (seller != null && !StringUtils.isEmpty(seller.getStatus())) {
                                criteria.andEqualTo("status", seller.getStatus());
                            }
                            if (seller != null && !StringUtils.isEmpty(seller.getName())) {
                                criteria.andLike("name", seller.getName());
                            }
                            if (seller != null && !StringUtils.isEmpty(seller.getNickName())) {
                                criteria.andLike("nickName", seller.getNickName());
                            }
                            sellerMapper.selectByExample(example);
                        }
                    });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
