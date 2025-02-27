package com.yushijie.server.controller;

import com.yushijie.common.entity.User;
import com.yushijie.server.component.RocketSender;
import com.yushijie.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @Autowired
    private RocketSender rocketSender;

    @RequestMapping("/getName")
    public String getName(@RequestParam Integer userId) {
        if (1 == userId) {
            return userName;
        } else {
            return "Unknow UserId!";
        }
    }

    @RequestMapping("/add-yushijie")
    public Integer addYushijie() {
        User user = User.builder().username("yushijie").nickname("yushijie").password("yushijie").build();
        return userService.addUser(user);
    }

    @RequestMapping("/send-msg")
    public void sendMsg() {
        rocketSender.send();
    }

}
