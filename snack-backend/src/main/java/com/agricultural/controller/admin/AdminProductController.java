package com.agricultural.controller.admin;

import com.agricultural.common.Result;
import com.agricultural.entity.Product;
import com.agricultural.service.ProductService;
import com.agricultural.vo.ProductVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端 - 农产品管理控制器
 */
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    public Result<Page<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        // 管理员后台查询所有商品（包括下架的）
        Page<ProductVO> page = productService.getProductList(pageNum, pageSize, categoryId, keyword, null, null, true);
        return Result.success(page);
    }

    /**
     * 添加商品
     */
    @PostMapping("/add")
    public Result<Void> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return Result.success();
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<Void> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        productService.updateProductStatus(id, status);
        return Result.success();
    }
}

