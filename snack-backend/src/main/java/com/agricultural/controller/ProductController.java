package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.service.ProductService;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 农产品控制器（用户端）
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping("/list")
    public Result<Page<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 用户端只查询上架商品
        Page<ProductVO> page = productService.getProductList(pageNum, pageSize, categoryId, keyword, sortBy, userId, false);
        return Result.success(page);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/detail/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ProductVO product = productService.getProductDetail(id, userId);
        return Result.success(product);
    }

    /**
     * 按分类获取商品
     */
    @GetMapping("/category/{categoryId}")
    public Result<Page<ProductVO>> getProductByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 用户端只查询上架商品
        Page<ProductVO> page = productService.getProductList(pageNum, pageSize, categoryId, null, null, userId, false);
        return Result.success(page);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<Page<ProductVO>> searchProduct(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 用户端只查询上架商品
        Page<ProductVO> page = productService.getProductList(pageNum, pageSize, null, keyword, null, userId, false);
        return Result.success(page);
    }
}

