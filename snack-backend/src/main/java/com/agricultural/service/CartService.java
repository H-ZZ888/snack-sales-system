package com.agricultural.service;

import com.agricultural.entity.Cart;
import com.agricultural.vo.CartVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {

    /**
     * 获取购物车列表
     */
    List<CartVO> getCartList(Long userId);

    /**
     * 添加到购物车
     */
    void addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    void updateCartQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 删除购物车商品
     */
    void deleteCart(Long userId, Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}

