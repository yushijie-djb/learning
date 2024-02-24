package com.yushijie.common.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class User implements Serializable {

    private Integer id;

    private String username;

    private String nickname;

    private String password;

}
