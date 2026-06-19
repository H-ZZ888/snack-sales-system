package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理端 - 数据统计控制器
 */
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 销售统计
     */
    @GetMapping("/sales")
    public Result<Map<String, Object>> getSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> statistics = statisticsService.getSalesStatistics(startDate, endDate);
        return Result.success(statistics);
    }

    /**
     * 用户统计
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = statisticsService.getUserStatistics();
        return Result.success(statistics);
    }
}
