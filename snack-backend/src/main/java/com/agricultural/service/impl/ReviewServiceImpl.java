package com.agricultural.service.impl;

import com.agricultural.entity.OrderItem;
import com.agricultural.entity.Orders;
import com.agricultural.entity.Product;
import com.agricultural.entity.Review;
import com.agricultural.entity.User;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.OrderItemMapper;
import com.agricultural.mapper.OrdersMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.mapper.ReviewMapper;
import com.agricultural.mapper.UserMapper;
import com.agricultural.service.ReviewService;
import com.agricultural.vo.ReviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addReview(Long userId, Review review) {
        // 验证订单是否存在且属于该用户
        Orders order = ordersMapper.selectById(review.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单是否已完成
        if (!"已完成".equals(order.getOrderStatus())) {
            throw new BusinessException("订单未完成，无法评价");
        }

        // 验证是否已评价
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, review.getOrderId())
                .eq(Review::getProductId, review.getProductId())
                .eq(Review::getUserId, userId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("已评价过该商品");
        }

        // 验证订单中是否包含该商品
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, review.getOrderId())
                .eq(OrderItem::getProductId, review.getProductId());
        if (orderItemMapper.selectCount(itemWrapper) == 0) {
            throw new BusinessException("订单中不包含该商品");
        }

        review.setUserId(userId);
        this.save(review);
    }

    @Override
    public Page<ReviewVO> getProductReviewList(Long productId, Integer pageNum, Integer pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId);
        wrapper.orderByDesc(Review::getCreateTime);
        Page<Review> reviewPage = this.page(page, wrapper);

        // 转换为ReviewVO，包含用户信息
        Page<ReviewVO> voPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<ReviewVO> voList = new ArrayList<>();

        for (Review review : reviewPage.getRecords()) {
            ReviewVO vo = new ReviewVO();
            BeanUtils.copyProperties(review, vo);

            // 查询用户信息（用户名和头像）
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<ReviewVO> getAllReviewList(Integer pageNum, Integer pageSize, String keyword, Integer rating) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Review::getContent, keyword);
        }

        // 评分筛选
        if (rating != null && rating > 0) {
            wrapper.eq(Review::getRating, rating);
        }

        wrapper.orderByDesc(Review::getCreateTime);
        Page<Review> reviewPage = this.page(page, wrapper);

        // 转换为ReviewVO
        Page<ReviewVO> voPage = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        List<ReviewVO> voList = new ArrayList<>();

        for (Review review : reviewPage.getRecords()) {
            ReviewVO vo = new ReviewVO();
            BeanUtils.copyProperties(review, vo);

            // 查询商品名称
            Product product = productMapper.selectById(review.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
            }

            // 查询用户信息（用户名和头像）
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                vo.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!this.removeById(reviewId)) {
            throw new BusinessException("删除失败");
        }
    }


    @Override
    public void deleteUserReview(Long userId, Long reviewId) {
        // 查询评价是否存在
        Review review = this.getById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }

        // 验证评价是否属于该用户
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人的评价");
        }

        // 删除评价
        if (!this.removeById(reviewId)) {
            throw new BusinessException("删除失败");
        }
    }

}

