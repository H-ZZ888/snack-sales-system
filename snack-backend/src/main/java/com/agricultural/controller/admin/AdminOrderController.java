package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.service.OrderService;
import com.agricultural.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 订单管理控制器
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public Result<Page<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderStatus) {
        Page<OrderVO> page = orderService.getAllOrderList(pageNum, pageSize, keyword, orderStatus);
        return Result.success(page);
    }

    /**
     * 订单发货
     */
    @PutMapping("/ship/{id}")
    public Result<Void> shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success();
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success();
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/updateStatus/{id}")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String orderStatus) {
        orderService.updateOrderStatus(id, orderStatus);
        return Result.success();
    }
}

