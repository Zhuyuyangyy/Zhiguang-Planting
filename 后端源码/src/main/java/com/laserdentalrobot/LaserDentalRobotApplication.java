package com.laserdentalrobot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类 没啥好说的
 */
@SpringBootApplication
@MapperScan("com.laserdentalrobot.mapper")
public class LaserDentalRobotApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LaserDentalRobotApplication.class, args);
    }
}
