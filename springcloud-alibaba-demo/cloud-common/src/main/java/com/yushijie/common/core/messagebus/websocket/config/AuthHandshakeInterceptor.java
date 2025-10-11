package com.yushijie.common.core.messagebus.websocket.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.keptdata.one2data.core.messagebus.websocket.common.WsConst;
import cn.keptdata.one2data.core.messagebus.websocket.core.WsAuthenticator;
import cn.keptdata.one2data.core.messagebus.websocket.core.WsPrincipal;
import cn.keptdata.one2data.util.extra.servlet.ServletUtils;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket 连接认证拦截
 * @date 2021/7/24 10:38
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Autowired
    private WsAuthenticator wsAuthenticator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("AuthHandshakeInterceptor beforeHandshake");
        }

        // 通过url的query参数获取Ws认证参数
        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest)request;
        HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();

        // WebSocket AccessToken
        String wsAccessToken = ServletUtils.getParam(servletRequest, WsConst.WS_TOKEN_KEY);

        boolean isValidAccess = false;
        try {
            // 认证token 是否合法
            WsPrincipal wsPrincipal = wsAuthenticator.authenticate(wsAccessToken);
            isValidAccess = true;

            // 认证成功加入上下文
            String tenantUuid = wsPrincipal.getTenantUuid();
            String userUuid = wsPrincipal.getUserUuid();

            attributes.put("tenantUuid", tenantUuid);
            attributes.put("userUuid", userUuid);
            attributes.put("identifier", wsAccessToken);

        } catch (Exception e) {
            log.error("WS 连接认证失败,原因：{}", e.getMessage());
        }

        return isValidAccess;
    }

    /**
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     * @return void
     * @author Bob.Yang
     * @description WebSocket 连接完成之后的处理流程
     * @date 2023-03-03 11:43
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception exception) {
        // do Nothing
        if (log.isDebugEnabled()) {
            log.debug("AuthHandshakeInterceptor afterHandshake");
        }
    }

}
