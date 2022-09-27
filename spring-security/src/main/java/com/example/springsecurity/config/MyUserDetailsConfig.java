package com.example.springsecurity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author yushijie
 * @version 1.0
 * @description config
 * @date 2022/9/27 14:11
 */
@ConditionalOnClass(UserDetailsService.class)
@Configuration(proxyBeanMethods = false)
public class MyUserDetailsConfig {


}
