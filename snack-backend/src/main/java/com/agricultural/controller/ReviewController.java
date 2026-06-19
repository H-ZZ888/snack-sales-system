package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.Review;
import com.agricultural.exception.BusinessException;
import com.agricultural.service.ReviewService;
import com.agricultural.vo.ReviewVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 添加评价
     */
    @PostMapping("/add")
    public Result<Void> addReview(HttpServletRequest request, @RequestBody Review review) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 验证必填字段
            if (review.getOrderId() == null) {
                return Result.error("订单ID不能为空");
            }
            if (review.getProductId() == null) {
                return Result.error("商品ID不能为空");
            }
            if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
                return Result.error("评分必须在1-5之间");
            }
            if (review.getContent() == null || review.getContent().trim().isEmpty()) {
                return Result.error("评价内容不能为空");
            }
            
            reviewService.addReview(userId, review);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交评价失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/product/{productId}")
    public Result<Page<ReviewVO>> getProductReviewList(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ReviewVO> page = reviewService.getProductReviewList(productId, pageNum, pageSize);
        return Result.success(page);
    }

}

