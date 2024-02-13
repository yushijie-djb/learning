package com.yushijie.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author yushijie
 * @version 1.0
 * @description 用户客户端 调用服务端服务
 * @date 2023/7/27 18:08
 */
@SpringBootApplication
@EnableFeignClients
public class UserClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserClientApplication.class, args);
    }
}
