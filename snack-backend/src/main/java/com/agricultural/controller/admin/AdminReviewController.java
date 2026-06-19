package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.service.ReviewService;
import com.agricultural.vo.ReviewVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 评价管理控制器
 */
@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取评价列表
     */
    @GetMapping("/list")
    public Result<Page<ReviewVO>> getReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating) {
        Page<ReviewVO> page = reviewService.getAllReviewList(pageNum, pageSize, keyword, rating);
        return Result.success(page);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return Result.success();
    }
}

