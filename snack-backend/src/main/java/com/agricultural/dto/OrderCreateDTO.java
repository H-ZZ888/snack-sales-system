package com.agricultural.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单商品列表
     */
    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemDTO> items;

    /**
     * 订单商品DTO
     */
    @Data
    public static class OrderItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        /**
         * 购买数量
         */
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;
    }
}

