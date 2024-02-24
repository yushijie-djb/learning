package com.yushijie.server.service;

import com.yushijie.common.entity.User;

/**
* @author 余世杰
* @description 针对表【user】的数据库操作Service
* @createDate 2024-02-22 20:07:23
*/
public interface UserService {
    int addUser(User user);
}
