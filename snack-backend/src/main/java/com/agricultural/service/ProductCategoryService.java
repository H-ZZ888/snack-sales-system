package com.agricultural.service;

import com.agricultural.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 农产品分类服务接口
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取所有分类列表
     */
    List<ProductCategory> getAllCategories();

    /**
     * 添加分类
     */
    void addCategory(ProductCategory category);

    /**
     * 更新分类
     */
    void updateCategory(ProductCategory category);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
}

