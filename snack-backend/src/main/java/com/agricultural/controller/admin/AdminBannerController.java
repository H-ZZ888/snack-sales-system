package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.entity.Banner;
import com.agricultural.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/banner")
public class AdminBannerController {
    
    @Autowired
    private BannerService bannerService;
    
    /**
     * 分页查询轮播图
     */
    @GetMapping("/list")
    public Result<Page<Banner>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        
        Page<Banner> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        
        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");
        
        Page<Banner> result = bannerService.page(page, queryWrapper);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询轮播图
     */
    @GetMapping("/{id}")
    public Result<Banner> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return Result.success(banner);
    }
    
    /**
     * 添加轮播图
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Banner banner) {
        // 如果没有设置排序号，设置为最大值+1
        if (banner.getSortOrder() == null) {
            QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("sort_order").last("LIMIT 1");
            Banner lastBanner = bannerService.getOne(queryWrapper);
            banner.setSortOrder(lastBanner != null ? lastBanner.getSortOrder() + 1 : 1);
        }
        
        // 默认状态为启用
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        
        bannerService.save(banner);
        return Result.success();
    }
    
    /**
     * 更新轮播图
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success();
    }
    
    /**
     * 删除轮播图
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }
    
    /**
     * 批量删除轮播图
     */
    @DeleteMapping("/deleteBatch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        bannerService.removeByIds(ids);
        return Result.success();
    }
    
    /**
     * 更新轮播图状态
     */
    @PutMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        bannerService.updateById(banner);
        return Result.success();
    }
}
