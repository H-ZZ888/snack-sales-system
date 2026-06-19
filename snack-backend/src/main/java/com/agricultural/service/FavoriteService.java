package com.agricultural.service;

import com.agricultural.entity.Favorite;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 收藏服务接口
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 获取收藏列表
     */
    Page<ProductVO> getFavoriteList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 添加收藏
     * @return 收藏记录ID
     */
    Long addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     */
    void deleteFavorite(Long userId, Long favoriteId);

    /**
     * 判断是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 根据商品ID取消收藏
     */
    void deleteFavoriteByProductId(Long userId, Long productId);
}
