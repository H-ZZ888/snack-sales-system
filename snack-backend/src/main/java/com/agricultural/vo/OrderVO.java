package com.agricultural.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单详情列表
     */
    private List<OrderItemVO> items;

    /**
     * 订单详情VO
     */
    @Data
    public static class OrderItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private String unit;
        private Integer quantity;
        private BigDecimal totalAmount;
    }
}

