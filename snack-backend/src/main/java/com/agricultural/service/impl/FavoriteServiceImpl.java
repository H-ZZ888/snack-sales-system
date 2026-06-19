package com.agricultural.service.impl;

import com.agricultural.entity.Favorite;
import com.agricultural.entity.Product;
import com.agricultural.entity.ProductCategory;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.FavoriteMapper;
import com.agricultural.mapper.ProductCategoryMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.service.FavoriteService;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Override
    public Page<ProductVO> getFavoriteList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        Page<Favorite> favoritePage = this.page(page, wrapper);

        Page<ProductVO> voPage = new Page<>(pageNum, pageSize, favoritePage.getTotal());
        List<ProductVO> voList = favoritePage.getRecords().stream().map(favorite -> {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product == null) {
                return null;
            }

            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);

            // 获取分类名称
            ProductCategory category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            vo.setIsFavorite(true);
            // 设置收藏记录ID，用于删除
            vo.setFavoriteId(favorite.getId());
            return vo;
        }).filter(vo -> vo != null).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Long addFavorite(Long userId, Long productId) {
        // 验证商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            throw new BusinessException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        this.save(favorite);
        
        // 返回新创建的收藏记录ID
        return favorite.getId();
    }

    @Override
    public void deleteFavorite(Long userId, Long favoriteId) {
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            throw new BusinessException("收藏记录不存在");
        }

        this.removeById(favoriteId);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        return this.count(wrapper) > 0;
    }

    @Override
    public void deleteFavoriteByProductId(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId);
        if (this.count(wrapper) == 0) {
            throw new BusinessException("收藏记录不存在");
        }
        this.remove(wrapper);
    }
}
