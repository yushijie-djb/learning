package com.yushijie.common.core.messagebus.websocket.receiver.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.ProcessSubscribeStrategy;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.context.ProcessSubscribeStrategyContext;

/**
 * @description 订阅消息接收策略工厂
 * @author HuangBing
 * @date 2024/5/11 上午11:59
 * @version 1.0
 */
@Component
public class MessageStrategyFactory{

    private static final Logger log = LoggerFactory.getLogger(MessageStrategyFactory.class);
    private final Map<String, ProcessSubscribeStrategyContext> strategyContextMap = new ConcurrentHashMap<>();

    /**
     * @description: 根据type获取对应策略
     * @author: HuangBing
     * @date: 2024/5/11 下午2:18
     * @param serviceType 服务类型
     * @return: cn.keptdata.one2data.header.message.receiver.strategy.context.ProcessSubscribeStrategyContext
     **/
    public ProcessSubscribeStrategyContext getStrategyContext(String serviceType) {
        return strategyContextMap.get(serviceType);
    }

    @Bean
    Map<String, ProcessSubscribeStrategyContext> setBusinessApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ProcessSubscribeStrategy> beansOfType =
            applicationContext.getBeansOfType(ProcessSubscribeStrategy.class);
        for (ProcessSubscribeStrategy subscribeStrategy : beansOfType.values()) {
            strategyContextMap.put(subscribeStrategy.serviceType(), new ProcessSubscribeStrategyContext(subscribeStrategy));
        }
        return strategyContextMap;
    }
}
