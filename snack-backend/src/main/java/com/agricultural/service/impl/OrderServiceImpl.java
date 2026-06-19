package com.agricultural.service.impl;

import com.agricultural.dto.OrderCreateDTO;
import com.agricultural.entity.OrderItem;
import com.agricultural.entity.Orders;
import com.agricultural.entity.Product;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.OrderItemMapper;
import com.agricultural.mapper.OrdersMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.service.OrderService;
import com.agricultural.service.ProductService;
import com.agricultural.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, OrderCreateDTO orderDTO) {
        // 计算订单总金额
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setReceiverName(orderDTO.getReceiverName());
        order.setReceiverPhone(orderDTO.getReceiverPhone());
        order.setReceiverAddress(orderDTO.getReceiverAddress());
        order.setRemark(orderDTO.getRemark());
        order.setPayStatus("未支付");
        order.setOrderStatus("未发货");

        // 处理订单商品
        orderDTO.getItems().forEach(item -> {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在：ID=" + item.getProductId());
            }

            if (!"上架".equals(product.getStatus())) {
                throw new BusinessException("商品已下架：" + product.getName());
            }

            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("商品库存不足：" + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount.updateAndGet(v -> v.add(itemTotal));
        });

        order.setTotalAmount(totalAmount.get());
        this.save(order);

        // 创建订单详情
        orderDTO.getItems().forEach(item -> {
            Product product = productMapper.selectById(item.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(product.getPrice().multiply(new BigDecimal(item.getQuantity())));

            orderItemMapper.insert(orderItem);

            // 减少库存
            productService.updateStock(product.getId(), item.getQuantity());
        });

        return order.getId();
    }

    @Override
    public Page<OrderVO> getOrderList(Long userId, Integer pageNum, Integer pageSize, String orderStatus, String payStatus) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);

        // 隐藏用户已删除的订单
        wrapper.and(w -> w.notLike(Orders::getRemark, "#用户已删除#").or().isNull(Orders::getRemark));

        // 按订单状态筛选
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(Orders::getOrderStatus, orderStatus);
        }

        // 按支付状态筛选
        if (StringUtils.hasText(payStatus)) {
            wrapper.eq(Orders::getPayStatus, payStatus);
        }

        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> orderPage = this.page(page, wrapper);

        return convertToVOPage(orderPage);
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否已被用户删除
        String remark = order.getRemark();
        if (remark != null && remark.contains("#用户已删除#")) {
            throw new BusinessException("订单不存在");
        }

        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (!"未支付".equals(order.getPayStatus())) {
            throw new BusinessException("订单已支付");
        }

        if ("已取消".equals(order.getOrderStatus())) {
            throw new BusinessException("订单已取消");
        }

        order.setPayStatus("已支付");
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);

        // 增加销量
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        items.forEach(item -> {
            productService.increaseSales(item.getProductId(), item.getQuantity());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if ("已取消".equals(order.getOrderStatus())) {
            throw new BusinessException("订单已取消");
        }

        if ("已完成".equals(order.getOrderStatus())) {
            throw new BusinessException("订单已完成，无法取消");
        }

        order.setOrderStatus("已取消");
        this.updateById(order);

        // 恢复库存（如果已支付，需要恢复销量）
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        items.forEach(item -> {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if ("已支付".equals(order.getPayStatus())) {
                    product.setSales(product.getSales() - item.getQuantity());
                }
                productMapper.updateById(product);
            }
        });
    }

    @Override
    public void receiveOrder(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (!"已发货".equals(order.getOrderStatus())) {
            throw new BusinessException("订单未发货");
        }

        order.setOrderStatus("已完成");
        order.setFinishTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void deleteOrderByUser(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 允许删除已取消或已完成的订单
        if (!"已取消".equals(order.getOrderStatus()) && !"已完成".equals(order.getOrderStatus())) {
            throw new BusinessException("只能删除已取消或已完成的订单");
        }

        // 不物理删除，通过追加备注实现用户端隐藏，管理员端可见
        String remark = order.getRemark();
        if (remark == null) {
            order.setRemark("#用户已删除#");
        } else if (!remark.contains("#用户已删除#")) {
            order.setRemark(remark + " #用户已删除#");
        }
        
        this.updateById(order);
    }

    @Override
    public void shipOrder(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"已支付".equals(order.getPayStatus())) {
            throw new BusinessException("订单未支付");
        }

        if (!"未发货".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        order.setOrderStatus("已发货");
        order.setShipTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 删除订单详情
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        orderItemMapper.delete(wrapper);

        // 删除订单
        this.removeById(orderId);
    }

    @Override
    public Page<OrderVO> getAllOrderList(Integer pageNum, Integer pageSize, String keyword, String orderStatus) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Orders::getOrderNo, keyword)
                    .or().like(Orders::getReceiverName, keyword)
                    .or().like(Orders::getReceiverPhone, keyword);
        }

        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(Orders::getOrderStatus, orderStatus);
        }

        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> orderPage = this.page(page, wrapper);

        return convertToVOPage(orderPage);
    }

    @Override
    public void updateOrderStatus(Long orderId, String orderStatus) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单状态
        if (!("未发货".equals(orderStatus) || "已发货".equals(orderStatus) || 
              "已完成".equals(orderStatus) || "已取消".equals(orderStatus))) {
            throw new BusinessException("无效的订单状态");
        }

        // 更新订单状态
        order.setOrderStatus(orderStatus);
        
        // 根据状态更新时间
        if ("已发货".equals(orderStatus)) {
            order.setShipTime(LocalDateTime.now());
        } else if ("已完成".equals(orderStatus)) {
            order.setFinishTime(LocalDateTime.now());
        }
        
        this.updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 9000) + 1000);
        return "ORD" + timestamp + random;
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 获取订单详情
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        List<OrderVO.OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            
            // 补充商品单位信息
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                itemVO.setUnit(product.getUnit());
            }
            
            return itemVO;
        }).toList();

        vo.setItems(itemVOs);
        return vo;
    }

    /**
     * 转换为VO分页
     */
    private Page<OrderVO> convertToVOPage(Page<Orders> orderPage) {
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream().map(this::convertToVO).toList();
        voPage.setRecords(voList);
        return voPage;
    }
}

