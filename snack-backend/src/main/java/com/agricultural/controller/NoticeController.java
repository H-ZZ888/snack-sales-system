package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.Notice;
import com.agricultural.service.NoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取公告列表
     */
    @GetMapping("/list")
    public Result<Page<Notice>> getNoticeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        Page<Notice> page = noticeService.getNoticeList(pageNum, pageSize, keyword, type);
        return Result.success(page);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeDetail(id);
        return Result.success(notice);
    }
}

