package com.agricultural.service.impl;

import com.agricultural.entity.Favorite;
import com.agricultural.entity.Product;
import com.agricultural.entity.ProductCategory;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.FavoriteMapper;
import com.agricultural.mapper.ProductCategoryMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.service.ProductService;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 农产品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Page<ProductVO> getProductList(Integer pageNum, Integer pageSize, Long categoryId, String keyword, String sortBy, Long userId, Boolean includeOffShelf) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword)
                    .or().like(Product::getOrigin, keyword);
        }

        // 状态筛选：如果不包含下架商品，只查询上架商品
        if (includeOffShelf == null || !includeOffShelf) {
            wrapper.eq(Product::getStatus, "上架");
        }
        
        // 排序
        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        Page<Product> productPage = this.page(page, wrapper);

        // 转换为VO
        Page<ProductVO> voPage = new Page<>(pageNum, pageSize, productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);

            // 获取分类名称
            ProductCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            // 判断是否已收藏
            if (userId != null) {
                LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
                favoriteWrapper.eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, product.getId());
                Favorite favorite = favoriteMapper.selectOne(favoriteWrapper);
                if (favorite != null) {
                    vo.setIsFavorite(true);
                    vo.setFavoriteId(favorite.getId());
                } else {
                    vo.setIsFavorite(false);
                    vo.setFavoriteId(null);
                }
            } else {
                vo.setIsFavorite(false);
                vo.setFavoriteId(null);
            }

            return vo;
        }).toList());

        return voPage;
    }

    @Override
    public ProductVO getProductDetail(Long productId, Long userId) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // 获取分类名称
        ProductCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 判断是否已收藏
        if (userId != null) {
            LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
            favoriteWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId);
            Favorite favorite = favoriteMapper.selectOne(favoriteWrapper);
            if (favorite != null) {
                vo.setIsFavorite(true);
                vo.setFavoriteId(favorite.getId());
            } else {
                vo.setIsFavorite(false);
                vo.setFavoriteId(null);
            }
        } else {
            vo.setIsFavorite(false);
            vo.setFavoriteId(null);
        }

        return vo;
    }

    @Override
    public void addProduct(Product product) {
        // 验证分类是否存在
        ProductCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        product.setSales(0);
        this.save(product);
    }

    @Override
    public void updateProduct(Product product) {
        Product existProduct = this.getById(product.getId());
        if (existProduct == null) {
            throw new BusinessException("商品不存在");
        }

        // 验证分类是否存在
        ProductCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        // 销量不允许修改
        product.setSales(null);
        this.updateById(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!this.removeById(productId)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateProductStatus(Long productId, String status) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        this.updateById(product);
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new BusinessException("库存不足");
        }

        product.setStock(newStock);
        this.updateById(product);
    }

    @Override
    public void increaseSales(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setSales(product.getSales() + quantity);
        this.updateById(product);
    }
}

