package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.entity.Notice;
import com.agricultural.service.NoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 公告管理控制器
 */
@RestController
@RequestMapping("/admin/notice")
public class AdminNoticeController {

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
     * 添加公告
     */
    @PostMapping("/add")
    public Result<Void> addNotice(@RequestBody Notice notice) {
        noticeService.addNotice(notice);
        return Result.success();
    }

    /**
     * 更新公告
     */
    @PutMapping("/update")
    public Result<Void> updateNotice(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return Result.success();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
}

