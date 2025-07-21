package com.projectdelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.projectdelivery.repository")
public class ProjectDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectDeliveryApplication.class, args);
    }
} 