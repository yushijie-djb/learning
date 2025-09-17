package com.yushijie.server.service.impl;


import com.yushijie.api.UserDubboApi;
import com.yushijie.common.entity.User;
import com.yushijie.server.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DubboService
public class UserDubboApiImpl implements UserDubboApi {

    @Autowired
    private UserService userService;

    @Override
    public void sayHelloDubbo() {
        System.out.println("Hello Dubbo");
    }

    @Override
    public void addYushijie() {
        User user = User.builder().username("yushijie").nickname("yu").password("123456").build();
        userService.addUser(user);
    }
}
