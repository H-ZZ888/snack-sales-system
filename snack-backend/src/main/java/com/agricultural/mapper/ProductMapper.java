package com.agricultural.mapper;

import com.agricultural.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 农产品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

