package com.yushijie.client.service.impl;

import com.yushijie.client.mapper.UserClientMapper;
import com.yushijie.client.service.UserClientService;
import com.yushijie.common.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



/**
* @author 余世杰
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-02-22 20:07:23
*/
@Service
public class UserClientServiceImpl implements UserClientService {

    @Resource
    private UserClientMapper userClientMapper;


    @Override
    public void deleteUser() {
        userClientMapper.deleteAll();
    }
}




