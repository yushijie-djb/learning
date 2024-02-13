package com.yushijie.service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class UserServiceController {

    @Value("${user.name}")
    private String userName;

    @RequestMapping("/getName")
    public String getName(@RequestParam Integer userId) {
        if (1 == userId) {
            return userName;
        } else {
            return "Unknow UserId!";
        }
    }
}
