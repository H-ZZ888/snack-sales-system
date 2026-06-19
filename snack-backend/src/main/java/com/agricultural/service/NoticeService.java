package com.agricultural.service;

import com.agricultural.entity.Notice;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 系统公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告列表
     */
    Page<Notice> getNoticeList(Integer pageNum, Integer pageSize, String keyword, String type);

    /**
     * 获取公告详情
     */
    Notice getNoticeDetail(Long noticeId);

    /**
     * 添加公告（管理员）
     */
    void addNotice(Notice notice);

    /**
     * 更新公告（管理员）
     */
    void updateNotice(Notice notice);

    /**
     * 删除公告（管理员）
     */
    void deleteNotice(Long noticeId);
}

