package com.agricultural.service.impl;

import com.agricultural.entity.Notice;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.NoticeMapper;
import com.agricultural.service.NoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public Page<Notice> getNoticeList(Integer pageNum, Integer pageSize, String keyword, String type) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 按关键词搜索（标题或内容）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword)
                    .or().like(Notice::getContent, keyword));
        }
        
        wrapper.orderByDesc(Notice::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Notice getNoticeDetail(Long noticeId) {
        Notice notice = this.getById(noticeId);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    public void addNotice(Notice notice) {
        this.save(notice);
    }

    @Override
    public void updateNotice(Notice notice) {
        Notice existNotice = this.getById(notice.getId());
        if (existNotice == null) {
            throw new BusinessException("公告不存在");
        }
        this.updateById(notice);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        if (!this.removeById(noticeId)) {
            throw new BusinessException("删除失败");
        }
    }
}

