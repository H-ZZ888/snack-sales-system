package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.entity.Article;
import com.agricultural.service.ArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资讯控制器
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取资讯列表
     */
    @GetMapping("/list")
    public Result<Page<Article>> getArticleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<Article> page = articleService.getArticleList(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    /**
     * 获取资讯详情
     */
    @GetMapping("/detail/{id}")
    public Result<Article> getArticleDetail(@PathVariable Long id) {
        Article article = articleService.getArticleDetail(id);
        return Result.success(article);
    }
}

