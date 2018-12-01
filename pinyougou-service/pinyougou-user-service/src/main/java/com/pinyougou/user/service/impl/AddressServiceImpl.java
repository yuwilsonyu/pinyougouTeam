package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 地址服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-23<p>
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(Address address) {
        try {
            addressMapper.insert(address);
        } catch (Exception e) {
            throw  new RuntimeException();
        }
    }

    @Override
    public void update(Address address) {
        try {
            addressMapper.updateByPrimaryKey(address);
        } catch (Exception e) {
            throw  new RuntimeException();
        }
    }

    @Override
    public void delete(Serializable id) {
        try {
            addressMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw  new RuntimeException();
        }
    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    @Override
    public List<Address> findAddressByUser(String userId) {
        try{
            // SELECT * FROM `tb_address` WHERE user_id = ?
            Address address = new Address();
            address.setUserId(userId);
            return addressMapper.select(address);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
