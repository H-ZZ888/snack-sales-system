package com.agricultural.config;

import com.agricultural.interceptor.JwtInterceptor;
import com.agricultural.interceptor.OptionalJwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private OptionalJwtInterceptor optionalJwtInterceptor;

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "https://*.pages.dev",
                        "https://*.yourdomain.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可选JWT拦截器 - 用于登录可选的接口（有token就验证，没有token也放行）
        registry.addInterceptor(optionalJwtInterceptor)
                .addPathPatterns(
                        "/product/list",
                        "/product/detail/**",
                        "/product/search",
                        "/product/category/**"
                );

        // 必须登录的JWT拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/admin/register",
                        "/admin/login",
                        "/file/upload",
                        "/uploads/**",
                        "/snack/**",
                        "/logo/**",
                        "/banner/list",
                        "/product/list",
                        "/product/detail/**",
                        "/product/search",
                        "/product/category/**",
                        "/category/list",
                        "/article/list",
                        "/article/detail/**",
                        "/notice/list",
                        "/notice/detail/**",
                        "/review/product/**"
                );
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件访问路径
        // 使用绝对路径或者确保相对路径正确
        String absolutePath = new java.io.File(uploadPath).getAbsolutePath() + java.io.File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
        
        // 配置静态资源目录（用于存放示例图片等）
        String staticPath = new java.io.File("static/").getAbsolutePath() + java.io.File.separator;
        registry.addResourceHandler("/snack/**")
                .addResourceLocations("file:" + staticPath + "snack/");
        
        // 配置logo等公共资源
        registry.addResourceHandler("/logo/**")
                .addResourceLocations("classpath:/static/logo/");
    }
}

