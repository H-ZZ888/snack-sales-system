package com.agricultural.service;

import com.agricultural.dto.OrderCreateDTO;
import com.agricultural.entity.Orders;
import com.agricultural.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Orders> {

    /**
     * 创建订单
     */
    Long createOrder(Long userId, OrderCreateDTO orderDTO);

    /**
     * 获取订单列表
     */
    Page<OrderVO> getOrderList(Long userId, Integer pageNum, Integer pageSize, String orderStatus, String payStatus);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long userId, Long orderId);

    /**
     * 支付订单
     */
    void payOrder(Long userId, Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    void receiveOrder(Long userId, Long orderId);

    /**
     * 删除订单（用户端 - 仅限已取消订单）
     */
    void deleteOrderByUser(Long userId, Long orderId);

    /**
     * 发货（管理员）
     */
    void shipOrder(Long orderId);

    /**
     * 删除订单（管理员）
     */
    void deleteOrder(Long orderId);

    /**
     * 获取所有订单列表（管理员）
     */
    Page<OrderVO> getAllOrderList(Integer pageNum, Integer pageSize, String keyword, String orderStatus);

    /**
     * 更新订单状态（管理员）
     */
    void updateOrderStatus(Long orderId, String orderStatus);
}

