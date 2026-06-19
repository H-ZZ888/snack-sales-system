package com.agricultural.service;

import com.agricultural.entity.Product;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 农产品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     * @param includeOffShelf 是否包含下架商品（管理员后台使用）
     */
    Page<ProductVO> getProductList(Integer pageNum, Integer pageSize, Long categoryId, String keyword, String sortBy, Long userId, Boolean includeOffShelf);

    /**
     * 获取商品详情
     */
    ProductVO getProductDetail(Long productId, Long userId);

    /**
     * 添加商品
     */
    void addProduct(Product product);

    /**
     * 更新商品
     */
    void updateProduct(Product product);

    /**
     * 删除商品
     */
    void deleteProduct(Long productId);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long productId, String status);

    /**
     * 更新商品库存
     */
    void updateStock(Long productId, Integer quantity);

    /**
     * 增加销量
     */
    void increaseSales(Long productId, Integer quantity);
}

