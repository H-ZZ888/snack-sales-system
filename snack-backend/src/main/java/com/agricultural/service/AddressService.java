package com.agricultural.service;

import com.agricultural.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<Address> {

    /**
     * 获取用户地址列表
     */
    List<Address> getAddressList(Long userId);

    /**
     * 添加地址
     */
    void addAddress(Long userId, Address address);

    /**
     * 更新地址
     */
    void updateAddress(Long userId, Address address);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}

