package com.example.springsecurity.config;

import com.example.springsecurity.service.impl.MyUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author yushijie
 * @version 1.0
 * @description config
 * @date 2022/9/27 14:11
 */
@ConditionalOnClass(UserDetailsService.class)
@Configuration(proxyBeanMethods = false)
public class MyUserDetailsConfig {

    @Bean
    public UserDetailsService myMyUserDetailsService() {
        return new MyUserDetailsService();
    }

    /*@Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
}
