package com.agricultural.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品VO
 */
@Data
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品主图URL
     */
    private String image;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 单位
     */
    private String unit;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 产地
     */
    private String origin;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 状态
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 是否已收藏
     */
    private Boolean isFavorite;

    /**
     * 收藏记录ID（用于取消收藏）
     */
    private Long favoriteId;
}

