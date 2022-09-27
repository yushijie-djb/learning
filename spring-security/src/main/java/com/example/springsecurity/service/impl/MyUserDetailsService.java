package com.example.springsecurity.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * @author yushijie
 * @version 1.0
 * @description user details
 * @date 2022/9/27 14:18
 */
public class MyUserDetailsService implements UserDetailsService {

    /**
     * @author yushijie
     * @description 用户名随意 密码需要为1234567
     * @date 2022/9/27 16:49
     * @param username
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.builder()
                .username(username)
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()::encode)
                .password("1234567")
                .authorities("ROLE_ADMIN")
                .build();
    }
}