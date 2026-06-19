package com.agricultural.service.impl;

import com.agricultural.entity.Address;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.AddressMapper;
import com.agricultural.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.orderByDesc(Address::getIsDefault);
        wrapper.orderByDesc(Address::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, Address address) {
        address.setUserId(userId);

        // 如果设置为默认地址，需要将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Address::getUserId, userId);
            updateWrapper.set(Address::getIsDefault, 0);
            this.update(updateWrapper);
        } else {
            // 如果是第一个地址，自动设为默认
            LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Address::getUserId, userId);
            if (this.count(queryWrapper) == 0) {
                address.setIsDefault(1);
            } else {
                address.setIsDefault(0);
            }
        }

        this.save(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, Address address) {
        Address existAddress = this.getById(address.getId());
        if (existAddress == null || !existAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 如果设置为默认地址，需要将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Address::getUserId, userId);
            updateWrapper.ne(Address::getId, address.getId());
            updateWrapper.set(Address::getIsDefault, 0);
            this.update(updateWrapper);
        }

        address.setUserId(userId);
        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        this.removeById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 将其他地址设为非默认
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId);
        updateWrapper.set(Address::getIsDefault, 0);
        this.update(updateWrapper);

        // 设置当前地址为默认
        address.setIsDefault(1);
        this.updateById(address);
    }
}

