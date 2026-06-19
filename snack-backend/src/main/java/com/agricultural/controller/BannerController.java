package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.Banner;
import com.agricultural.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器（用户端）
 */
@RestController
@RequestMapping("/banner")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;
    
    /**
     * 获取启用的轮播图列表（按排序号排序）
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order")
                   .orderByDesc("create_time");
        List<Banner> banners = bannerService.list(queryWrapper);
        return Result.success(banners);
    }
}
