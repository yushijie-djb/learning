package com.yushijie.nacos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/7/25 17:38
 */
@RestController
@RefreshScope
@RequestMapping("/nacos")
public class NacosController {

    @Value("${china.dick}")
    private String name;

    @RequestMapping("/nacos")
    public String nacos() {
        return name;
    }

}
