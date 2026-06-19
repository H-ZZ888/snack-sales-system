package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.service.CartService;
import com.agricultural.vo.CartVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartVO> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }

    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public Result<Void> addToCart(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.addToCart(userId, productId, quantity);
        return Result.success();
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<Void> updateCartQuantity(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long cartId = Long.valueOf(params.get("cartId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.updateCartQuantity(userId, cartId, quantity);
        return Result.success();
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCart(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.deleteCart(userId, id);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success();
    }
}

