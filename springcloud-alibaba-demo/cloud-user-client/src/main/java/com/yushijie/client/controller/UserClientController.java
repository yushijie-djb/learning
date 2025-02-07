package com.yushijie.client.controller;

import com.yushijie.api.UserDubboApi;
import com.yushijie.client.feign.UserClientFeignService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClientController {

    @Autowired
    private UserClientFeignService userClientFeignService;

    @DubboReference
    private UserDubboApi userDubboApi;

    @RequestMapping("/sayHelloDubbo")
    public void sayHelloDubbo() {
       userDubboApi.sayHelloDubbo();
    }

    @RequestMapping("/sayName")
    public void sayName(@RequestParam Integer userId) {
        String name = userClientFeignService.getName(userId);

        System.out.println(name);
    }

    @RequestMapping("/add-yushijie")
    public Integer addYushijie() {
        return userClientFeignService.addYushijie();
    }

    @RequestMapping("/add-and-delete")
//    @GlobalTransactional
    public String addAndDelete() {

        userClientFeignService.addYushijie();
        // 同一个库 但是 不同进程 同样构成分布式事务场景 虽然userService 服务成功添加了yushijie
        // 但是userClient报错 TM告知TC-》RM回滚
        int i = 1 / 0;

        return "bingo!";
    }

}
