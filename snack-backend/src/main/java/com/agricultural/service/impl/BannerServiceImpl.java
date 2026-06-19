package com.agricultural.service.impl;

import com.agricultural.entity.Banner;
import com.agricultural.mapper.BannerMapper;
import com.agricultural.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 轮播图服务实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
}
