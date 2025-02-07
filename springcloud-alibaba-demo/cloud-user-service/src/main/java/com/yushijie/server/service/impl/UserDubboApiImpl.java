package com.yushijie.server.service.impl;


import com.yushijie.api.UserDubboApi;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserDubboApiImpl implements UserDubboApi {
    @Override
    public void sayHelloDubbo() {
        System.out.println("Hello Dubbo");
    }
}
