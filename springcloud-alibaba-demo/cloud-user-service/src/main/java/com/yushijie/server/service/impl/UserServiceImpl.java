package com.yushijie.server.service.impl;

import com.yushijie.common.entity.User;
import com.yushijie.server.service.UserService;
import com.yushijie.server.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author 余世杰
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-02-22 20:07:23
*/
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }
}




