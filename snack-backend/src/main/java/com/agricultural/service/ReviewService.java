package com.agricultural.service;

import com.agricultural.entity.Review;
import com.agricultural.vo.ReviewVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 评价服务接口
 */
public interface ReviewService extends IService<Review> {

    /**
     * 添加评价
     */
    void addReview(Long userId, Review review);

    /**
     * 获取商品评价列表
     */
    Page<ReviewVO> getProductReviewList(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 获取所有评价列表（管理员）
     */
    Page<ReviewVO> getAllReviewList(Integer pageNum, Integer pageSize, String keyword, Integer rating);

    /**
     * 删除评价（管理员）
     */
    void deleteReview(Long reviewId);


    /**
     * 删除用户自己的评价
     */
    void deleteUserReview(Long userId, Long reviewId);

}

