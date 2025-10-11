package com.yushijie.common.core.messagebus.websocket.receiver.strategy.aspect;

import java.util.Collections;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.content.ProgressSubscribe;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.ProcessSubscribeStrategy;

/**
 * @description 进度订阅策略切面类
 * @author HuangBing
 * @date 2024/5/14 上午10:06
 * @version 1.0
 */
@Aspect
@Component
public class ProcessSubscribeStrategyAspect {
    private static final Log log = LogFactory.getLog();

    /**
     * @description: 处理消息之前的操作
     * @param strategy
     * @param message
     * @return: void
     **/
    @Before(
        value = "execution(* cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.ProcessSubscribeStrategy.processMessage(..)) && target(strategy) && args(message)",
        argNames = "strategy,message")
    public void beforeProcessMessage(ProcessSubscribeStrategy strategy, ProgressSubscribe message) {
        try {
            strategy.beforeProcessMessage(message);
        }catch (Exception e) {
            log.error("Error processing subscribe UUIDs", e);
            message.setUuids(Collections.emptyList());
        }

    }
    /**
     * @description: 捕获processMessage方法抛出的异常并处理
     * @param strategy
     * @param message
     * @param ex
     * @return: void
     **/
    @AfterThrowing(
        pointcut = "execution(* cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.ProcessSubscribeStrategy.processMessage(..)) && target(strategy) && args(message)",
        throwing = "ex", argNames = "strategy,message,ex")
    public void afterThrowingProcessMessage(ProcessSubscribeStrategy strategy, ProgressSubscribe message,
        Exception ex) {
        try {
            strategy.afterThrowingProcessMessage(message);
        }catch (Exception e) {
            log.error("When Exception {} occurred,Error processing subscribe UUIDs",ex.getCause(), e);
        }

    }

}
