package com.yushijie.common.core.messagebus.websocket.config;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import cn.keptdata.one2data.core.messagebus.websocket.common.WsConst;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsSessionManager;
import cn.keptdata.one2data.header.dashboard.enumeration.WsOperateTypeEnum;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 保存WebSocket 信息
 * @date 2021/7/24 11:28
 */
@Component
public class AuthWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    private static final Logger log = LoggerFactory.getLogger(AuthWebSocketHandlerDecoratorFactory.class);

    @Autowired
    private WsSessionManager wsSessionManager;

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {

        if (log.isDebugEnabled()) {
            log.debug("AuthWebSocketHandlerDecoratorFactory decorate");
        }

        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                // wsClient && wsServer 建立连接之后,记录连接信息
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    String identifier = principal.getName();

                    if (log.isDebugEnabled()) {
                        log.debug("WEB | websocket client  establish sessionID:{} identifier:{}", session.getId(),
                            identifier);
                    }

                    // 加入websocketSession 连接池
                    wsSessionManager.add(session);
                }
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                // wsClient && wsServer 断开连接之后,记录信息
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    String identifier = principal.getName();
                    if (log.isDebugEnabled()) {
                        log.debug("WEB | websocket client closed sessionID:{} identifier:{}", session.getId(),
                            identifier);
                    }

                    // 加入websocketSession 连接池中 移除
                    wsSessionManager.remove(session);
                }

                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void handleMessage (WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                try {
                    String payload = (String) message.getPayload();
                    String[] parts = payload.split(WsConst.WS_PLAY_LOAD_REGEX);
                    // 获取操作类型
                    String operationType = parts[0];
                    // 获取topic
                    String topic = parts[1].replace(WsConst.WS_PLAY_LOAD_TOPIC_PREFIX, WsConst.WS_PLAY_LOAD_TOPIC_REPLACEMENT);
                    if (WsOperateTypeEnum.SUBSCRIBE.getCode().equals(operationType)) {
                        // 订阅
                        wsSessionManager.addTopicSubscribeCount(topic);
                    } else if (WsOperateTypeEnum.UNSUBSCRIBE.getCode().equals(operationType)) {
                        // 取消订阅
                        wsSessionManager.subtractTopicSubscribeCount(topic);
                    }
                } catch (Exception e) {
                    log.error("WEB | websocket topic handle error", e);
                }finally {
                    super.handleMessage(session, message);
                }
            }
        };

    }
}
