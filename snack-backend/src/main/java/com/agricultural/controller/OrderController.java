package com.agricultural.controller;

import com.agricultural.common.Result;
import com.agricultural.dto.OrderCreateDTO;
import com.agricultural.service.OrderService;
import com.agricultural.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（用户端）
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Long> createOrder(HttpServletRequest request, @Valid @RequestBody OrderCreateDTO orderDTO) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = orderService.createOrder(userId, orderDTO);
        return Result.success("订单创建成功", orderId);
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public Result<Page<OrderVO>> getOrderList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String payStatus) {
        Long userId = (Long) request.getAttribute("userId");
        Page<OrderVO> page = orderService.getOrderList(userId, pageNum, pageSize, orderStatus, payStatus);
        return Result.success(page);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{id}")
    public Result<OrderVO> getOrderDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        OrderVO order = orderService.getOrderDetail(userId, id);
        return Result.success(order);
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay/{id}")
    public Result<Void> payOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(userId, id);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    /**
     * 确认收货
     */
    @PutMapping("/receive/{id}")
    public Result<Void> receiveOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.receiveOrder(userId, id);
        return Result.success();
    }

    /**
     * 删除订单（仅限已取消订单）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteOrder(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.deleteOrderByUser(userId, id);
        return Result.success();
    }
}

