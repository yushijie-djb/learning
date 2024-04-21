package com.yushijie.server.mapper;

import com.yushijie.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author 余世杰
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-02-22 20:07:23
* @Entity com.yushijie.entity.User
*/
@Mapper
@Repository
public interface UserMapper {
    int insert(User user);
}




