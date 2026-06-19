package com.agricultural.service.impl;

import com.agricultural.entity.Product;
import com.agricultural.entity.ProductCategory;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.ProductCategoryMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.service.ProductCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 农产品分类服务实现类
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductCategory> getAllCategories() {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ProductCategory::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public void addCategory(ProductCategory category) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getName, category.getName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        this.save(category);
    }

    @Override
    public void updateCategory(ProductCategory category) {
        ProductCategory existCategory = this.getById(category.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名称是否与其他分类重复
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getName, category.getName())
                .ne(ProductCategory::getId, category.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        this.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 检查该分类下是否有商品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, categoryId);
        long count = productMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该分类下有商品，无法删除");
        }

        if (!this.removeById(categoryId)) {
            throw new BusinessException("删除失败");
        }
    }
}

