package com.yushijie.common.core.messagebus.websocket.config;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import cn.keptdata.one2data.core.messagebus.websocket.core.WsPrincipal;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 处理WS 握手请求
 * @date 2021/7/24 11:28
 */
@Component
public class WsPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger log = LoggerFactory.getLogger(WsPrincipalHandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
        Map<String, Object> attributes) {

        if (log.isDebugEnabled()) {
            log.debug("WsPrincipalHandshakeHandler determineUser");
        }

        // 从握手连接中获取WS会话登录标识
        String identifier = (String)attributes.get("identifier");
        String tenantUuid = (String)attributes.get("tenantUuid");
        String userUuid = (String)attributes.get("userUuid");

        return new WsPrincipal(identifier, tenantUuid, userUuid);
    }
}
