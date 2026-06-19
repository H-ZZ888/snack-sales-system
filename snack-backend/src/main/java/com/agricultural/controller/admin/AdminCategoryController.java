package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.entity.ProductCategory;
import com.agricultural.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 分类管理控制器
 */
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

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

    /**
     * 添加分类
     */
    @PostMapping("/add")
    public Result<Void> addCategory(@RequestBody ProductCategory category) {
        categoryService.addCategory(category);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/update")
    public Result<Void> updateCategory(@RequestBody ProductCategory category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}

