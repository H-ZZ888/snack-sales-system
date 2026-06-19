package com.agricultural.service.impl;

import com.agricultural.entity.Cart;
import com.agricultural.entity.Product;
import com.agricultural.exception.BusinessException;
import com.agricultural.mapper.CartMapper;
import com.agricultural.mapper.ProductMapper;
import com.agricultural.service.CartService;
import com.agricultural.vo.CartVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<CartVO> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> cartList = this.list(wrapper);

        return cartList.stream().map(cart -> {
            CartVO vo = new CartVO();
            BeanUtils.copyProperties(cart, vo);

            // 获取商品信息
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductImage(product.getImage());
                vo.setProductPrice(product.getPrice());
                vo.setProductUnit(product.getUnit());
                vo.setProductStock(product.getStock());
                vo.setTotalAmount(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            }

            return vo;
        }).toList();
    }

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 验证商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查库存
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 检查商品状态
        if (!"上架".equals(product.getStatus())) {
            throw new BusinessException("商品已下架");
        }

        // 查询购物车中是否已有该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart existCart = this.getOne(wrapper);

        if (existCart != null) {
            // 如果已存在，更新数量
            int newQuantity = existCart.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new BusinessException("库存不足");
            }
            existCart.setQuantity(newQuantity);
            this.updateById(existCart);
        } else {
            // 如果不存在，新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            this.save(cart);
        }
    }

    @Override
    public void updateCartQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        // 验证库存
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        cart.setQuantity(quantity);
        this.updateById(cart);
    }

    @Override
    public void deleteCart(Long userId, Long cartId) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }

        this.removeById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }
}

