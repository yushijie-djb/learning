package com.yushijie.common.core.messagebus.websocket.receiver.strategy.context;

import cn.keptdata.one2data.core.messagebus.websocket.receiver.content.ProgressSubscribe;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.ProcessSubscribeStrategy;

/**
 * @description 进度订阅策略上下文
 * @author HuangBing
 * @date 2024/5/9 下午6:50
 * @version 1.0
 */
public class ProcessSubscribeStrategyContext {
    private final ProcessSubscribeStrategy strategy;

    public ProcessSubscribeStrategyContext(ProcessSubscribeStrategy strategy) {
        this.strategy = strategy;
    }

    public void processMessage(ProgressSubscribe message) {
        strategy.processMessage(message);
    }
}
