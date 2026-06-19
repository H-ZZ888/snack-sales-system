package com.agricultural.mapper;

import com.agricultural.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资讯Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

