package com.example.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yushijie
 * @version 1.0
 * @description Demo
 * @date 2022/9/27 11:30
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("1")
    public String demo1() {
        return "demo1";
    }
}
