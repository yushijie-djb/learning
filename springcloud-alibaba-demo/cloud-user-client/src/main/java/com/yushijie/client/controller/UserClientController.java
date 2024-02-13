package com.yushijie.client.controller;

import com.yushijie.client.feign.UserClientFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClientController {

    @Autowired
    private UserClientFeignService userClientFeignService;

    @RequestMapping("/sayName")
    public void sayName(@RequestParam Integer userId) {
        String name = userClientFeignService.getName(userId);

        System.out.println(name);
    }
}
