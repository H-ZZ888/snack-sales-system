package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.entity.Article;
import com.agricultural.service.ArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 资讯管理控制器
 */
@RestController
@RequestMapping("/admin/article")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取资讯列表
     */
    @GetMapping("/list")
    public Result<Page<Article>> getArticleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        Page<Article> page = articleService.getAdminArticleList(pageNum, pageSize, keyword, category);
        return Result.success(page);
    }

    /**
     * 添加资讯
     */
    @PostMapping("/add")
    public Result<Void> addArticle(@RequestBody Article article) {
        articleService.addArticle(article);
        return Result.success();
    }

    /**
     * 更新资讯
     */
    @PutMapping("/update")
    public Result<Void> updateArticle(@RequestBody Article article) {
        articleService.updateArticle(article);
        return Result.success();
    }

    /**
     * 删除资讯
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }
}

