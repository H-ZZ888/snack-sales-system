package com.agricultural.service.impl;

import com.agricultural.entity.Article;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.ArticleMapper;
import com.agricultural.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 资讯服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public Page<Article> getArticleList(Integer pageNum, Integer pageSize, String category, String keyword) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（标题或内容）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword));
        }

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(Article::getCategory, category);
        }

        wrapper.orderByDesc(Article::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Article getArticleDetail(Long articleId) {
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException("资讯不存在");
        }

        // 增加浏览量（处理null值的情况）
        Integer currentViews = article.getViewCount();
        if (currentViews == null) {
            currentViews = 0;
        }
        article.setViewCount(currentViews + 1);
        this.updateById(article);

        return article;
    }

    @Override
    public void addArticle(Article article) {
        // 验证必填字段
        if (!StringUtils.hasText(article.getTitle())) {
            throw new BusinessException("资讯标题不能为空");
        }
        if (!StringUtils.hasText(article.getContent())) {
            throw new BusinessException("资讯内容不能为空");
        }
        if (!StringUtils.hasText(article.getCategory())) {
            throw new BusinessException("资讯分类不能为空");
        }
        if (!StringUtils.hasText(article.getImage())) {
            throw new BusinessException("封面图片不能为空");
        }
        
        // 初始化浏览量为0
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        
        // 保存文章
        boolean saved = this.save(article);
        System.out.println("保存文章结果: " + saved);
        System.out.println("保存的文章信息 - ID: " + article.getId() + 
                         ", 标题: " + article.getTitle() + 
                         ", 图片: " + article.getImage() + 
                         ", 分类: " + article.getCategory());
        
        if (!saved) {
            throw new BusinessException("保存文章失败");
        }
    }

    @Override
    public void updateArticle(Article article) {
        Article existArticle = this.getById(article.getId());
        if (existArticle == null) {
            throw new BusinessException("资讯不存在");
        }

        // 验证必填字段
        if (!StringUtils.hasText(article.getTitle())) {
            throw new BusinessException("资讯标题不能为空");
        }
        if (!StringUtils.hasText(article.getContent())) {
            throw new BusinessException("资讯内容不能为空");
        }
        if (!StringUtils.hasText(article.getCategory())) {
            throw new BusinessException("资讯分类不能为空");
        }
        if (!StringUtils.hasText(article.getImage())) {
            throw new BusinessException("封面图片不能为空");
        }

        System.out.println("更新前的文章信息 - ID: " + article.getId() + 
                         ", 标题: " + article.getTitle() + 
                         ", 图片: " + article.getImage() + 
                         ", 分类: " + article.getCategory());
        
        // 保留原有的浏览量（不允许通过编辑修改浏览量）
        article.setViewCount(existArticle.getViewCount());
        
        // 更新文章
        boolean updated = this.updateById(article);
        
        if (updated) {
            Article updatedArticle = this.getById(article.getId());
            System.out.println("更新后的文章信息 - ID: " + updatedArticle.getId() + 
                             ", 标题: " + updatedArticle.getTitle() + 
                             ", 图片: " + updatedArticle.getImage() + 
                             ", 分类: " + updatedArticle.getCategory());
        } else {
            throw new BusinessException("更新文章失败");
        }
    }

    @Override
    public void deleteArticle(Long articleId) {
        if (!this.removeById(articleId)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public Page<Article> getAdminArticleList(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（标题或内容）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword));
        }

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(Article::getCategory, category);
        }

        wrapper.orderByDesc(Article::getCreateTime);
        return this.page(page, wrapper);
    }
}

