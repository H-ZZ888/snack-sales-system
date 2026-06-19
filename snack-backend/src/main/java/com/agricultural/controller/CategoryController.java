package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.ProductCategory;
import com.agricultural.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public Result<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}

