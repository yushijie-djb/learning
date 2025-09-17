package com.yushijie.client.controller;

import com.yushijie.api.UserDubboApi;
import com.yushijie.client.feign.UserClientFeignService;
import com.yushijie.client.mapper.UserClientMapper;
import com.yushijie.common.entity.User;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClientController {

    @Autowired
    private UserClientFeignService userClientFeignService;

    @Autowired
    private UserClientMapper userClientMapper;

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

    @RequestMapping("add-yushijie-dubbo")
    @GlobalTransactional
    public void addYushijieDubbo() {
        // 插demo2库
        User user = User.builder().username("yushijie").nickname("yu").password("123456").build();
        userClientMapper.insert(user);

        // 插demo库
        userDubboApi.addYushijie();
        // 全局事务开启 DubboApi使用的demo2库回滚 本地demo库回滚 双库数据皆插入失败
        int i = 1/0;
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
