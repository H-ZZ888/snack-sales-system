package com.agricultural.service.impl;

import com.agricultural.entity.*;
import com.agricultural.mapper.*;
import com.agricultural.service.StatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getSalesStatistics(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        // 查询订单数据
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Orders::getPayStatus, "已支付");
        
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            orderWrapper.between(Orders::getCreateTime, start, end);
        }
        
        List<Orders> orders = ordersMapper.selectList(orderWrapper);

        // 按时间统计销售额和订单量
        Map<String, BigDecimal> salesByDate = new LinkedHashMap<>();
        Map<String, Integer> ordersByDate = new LinkedHashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        orders.forEach(order -> {
            String date = order.getCreateTime().format(dateFormatter);
            salesByDate.put(date, salesByDate.getOrDefault(date, BigDecimal.ZERO).add(order.getTotalAmount()));
            ordersByDate.put(date, ordersByDate.getOrDefault(date, 0) + 1);
        });

        // 按产品类别统计销量和销售额
        Map<String, Integer> quantityByCategory = new HashMap<>();
        Map<String, BigDecimal> amountByCategory = new HashMap<>();
        
        // 按商品统计销量（用于排行榜）
        Map<Long, Integer> quantityByProduct = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        
        orders.forEach(order -> {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            
            items.forEach(item -> {
                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    // 统计商品销量
                    quantityByProduct.put(product.getId(), 
                        quantityByProduct.getOrDefault(product.getId(), 0) + item.getQuantity());
                    productNames.put(product.getId(), product.getName());
                    
                    // 统计分类销量和销售额
                    ProductCategory category = categoryMapper.selectById(product.getCategoryId());
                    if (category != null) {
                        String categoryName = category.getName();
                        quantityByCategory.put(categoryName, quantityByCategory.getOrDefault(categoryName, 0) + item.getQuantity());
                        amountByCategory.put(categoryName, amountByCategory.getOrDefault(categoryName, BigDecimal.ZERO).add(item.getTotalAmount()));
                    }
                }
            });
        });

        // 生成商品销量排行榜（取前10名）
        List<Map<String, Object>> productRanking = quantityByProduct.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("productId", entry.getKey());
                item.put("productName", productNames.get(entry.getKey()));
                item.put("quantity", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());

        result.put("salesByDate", salesByDate);
        result.put("ordersByDate", ordersByDate);
        result.put("quantityByCategory", quantityByCategory);
        result.put("amountByCategory", amountByCategory);
        result.put("productRanking", productRanking);
        result.put("totalSales", orders.stream().map(Orders::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("totalOrders", orders.size());
        result.put("totalProducts", productMapper.selectCount(null).intValue());

        return result;
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();
        int totalUsers = userMapper.selectCount(null).intValue();
        result.put("totalUsers", totalUsers);
        return result;
    }
}

