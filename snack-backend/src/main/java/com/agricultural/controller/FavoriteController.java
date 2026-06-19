package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.service.FavoriteService;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<Page<ProductVO>> getFavoriteList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        Page<ProductVO> page = favoriteService.getFavoriteList(userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<Long> addFavorite(HttpServletRequest request, @RequestBody Map<String, Long> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = params.get("productId");
        Long favoriteId = favoriteService.addFavorite(userId, productId);
        return Result.success(favoriteId);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteFavorite(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.deleteFavorite(userId, id);
        return Result.success();
    }

    /**
     * 取消收藏（通过商品ID）
     */
    @DeleteMapping("/remove/{productId}")
    public Result<Void> removeFavoriteByProductId(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.deleteFavoriteByProductId(userId, productId);
        return Result.success();
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public Result<Boolean> checkFavorite(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isFavorite = favoriteService.isFavorite(userId, productId);
        return Result.success(isFavorite);
    }
}

