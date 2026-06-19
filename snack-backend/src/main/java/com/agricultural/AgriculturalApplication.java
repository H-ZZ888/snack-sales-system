package com.agricultural;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 零食销售管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.agricultural.mapper")
public class AgriculturalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriculturalApplication.class, args);
        System.out.println("========================================");
        System.out.println("零食销售管理系统启动成功!");
        System.out.println("========================================");
    }
}

