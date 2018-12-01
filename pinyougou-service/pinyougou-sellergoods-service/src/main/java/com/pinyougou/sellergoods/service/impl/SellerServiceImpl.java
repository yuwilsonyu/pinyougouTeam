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
 * SellerServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2018-10-30 16:10:23
 */
@Service(interfaceName = "com.pinyougou.service.SellerService")
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;


<<<<<<< Updated upstream
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
=======
    /** 商家申请入驻 */
    @Override
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        try {
            sellerMapper.updateByPrimaryKeySelective(seller);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
=======
        /*// 创建示范对象
        Example example = new Example(Seller.class);
        // 创建条件对象
        Example.Criteria criteria = example.createCriteria();
        // in条件
        criteria.andIn("name", Arrays.asList());*/
        try {
            sellerMapper.updateBymessage(seller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

>>>>>>> Stashed changes
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

<<<<<<< Updated upstream

}
=======
    /** 修改商家状态 */
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
        //查询用户名和密码
    @Override                                // 密码
    public Seller checkPassword(String username) {
        //select * from tb_seller where seller_id = 'admin';

        try{
            return sellerMapper.getPasswordBySellerId(username);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
     //更新用户密码
    @Override
    public boolean updateSellerPasword(String username, String newPassword) {
        try {
            //update tb_seller set password='456'where seller_id='123'
            //创建Seller 接收加密密码
            Seller seller = new Seller();
            seller.setPassword(newPassword);
            //创建通用Mapper
            Example example = new Example(Seller.class);
            Example.Criteria criteria = example.createCriteria();
            //根据条件更新
            criteria.andEqualTo("sellerId",username);
            //更新
            sellerMapper.updateByExampleSelective(seller, example);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
>>>>>>> Stashed changes
