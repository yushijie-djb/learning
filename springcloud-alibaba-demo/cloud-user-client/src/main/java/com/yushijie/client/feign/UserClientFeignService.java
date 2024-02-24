package com.yushijie.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")//请求user-service
public interface UserClientFeignService {

    @RequestMapping("/getName")
    public String getName(@RequestParam Integer userId);

    @RequestMapping("/add-yushijie")
    Integer addYushijie();

}
