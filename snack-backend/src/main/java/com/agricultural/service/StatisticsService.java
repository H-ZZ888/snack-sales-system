package com.agricultural.service;

import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /**
     * 销售统计
     */
    Map<String, Object> getSalesStatistics(String startDate, String endDate);

    /**
     * 用户统计
     */
    Map<String, Object> getUserStatistics();
}

