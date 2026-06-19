package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.Address;
import com.agricultural.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取收货地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Address> addressList = addressService.getAddressList(userId);
        return Result.success(addressList);
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/add")
    public Result<Void> addAddress(HttpServletRequest request, @RequestBody Address address) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.addAddress(userId, address);
        return Result.success();
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/update")
    public Result<Void> updateAddress(HttpServletRequest request, @RequestBody Address address) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.updateAddress(userId, address);
        return Result.success();
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAddress(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.deleteAddress(userId, id);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/setDefault/{id}")
    public Result<Void> setDefaultAddress(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }
}

