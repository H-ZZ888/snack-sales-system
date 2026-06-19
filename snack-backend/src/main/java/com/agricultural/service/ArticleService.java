package com.agricultural.service;

import com.agricultural.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 资讯服务接口
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询资讯列表
     */
    Page<Article> getArticleList(Integer pageNum, Integer pageSize, String category, String keyword);

    /**
     * 获取资讯详情
     */
    Article getArticleDetail(Long articleId);

    /**
     * 添加资讯（管理员）
     */
    void addArticle(Article article);

    /**
     * 更新资讯（管理员）
     */
    void updateArticle(Article article);

    /**
     * 删除资讯（管理员）
     */
    void deleteArticle(Long articleId);

    /**
     * 获取管理员端资讯列表（支持关键词搜索）
     */
    Page<Article> getAdminArticleList(Integer pageNum, Integer pageSize, String keyword, String category);
}

