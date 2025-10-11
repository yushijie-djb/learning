package com.yushijie.common.core.messagebus.websocket.receiver;

import javax.annotation.Resource;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import cn.keptdata.one2data.core.messagebus.websocket.receiver.content.ProgressSubscribe;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.factory.MessageStrategyFactory;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.strategy.context.ProcessSubscribeStrategyContext;
import cn.keptdata.one2data.util.string.StringUtils;

/**
 * @description WS消息接收器
 * @author HuangBing
 * @date 2024/5/9 下午6:31
 * @version 1.0
 */
@Controller
public class WsMessageReceiver {

    @Resource
    private MessageStrategyFactory messageStrategyFactory;

    /**
     * @description: 接收进度订阅消息
     * @author: HuangBing
     * @date: 2024/5/10 上午10:42
     * @param message 消息
     * @return: void
     **/
    @MessageMapping("/processSubscribe")
    public void receiveMessage(@Payload ProgressSubscribe message, @Header("simpSessionId") String sessionId) {
        // 服务类型
        String serviceType = message.getServiceType();
        // 业务名称
        if (StringUtils.isNotBlank(message.getBusinessName())) {
            serviceType = serviceType + message.getBusinessName();
        }
        // 处理接收到的消息
        ProcessSubscribeStrategyContext strategyContext = messageStrategyFactory.getStrategyContext(serviceType);
        if (strategyContext != null) {
            message.setSessionId(sessionId);
            strategyContext.processMessage(message);
        }
    }
}
