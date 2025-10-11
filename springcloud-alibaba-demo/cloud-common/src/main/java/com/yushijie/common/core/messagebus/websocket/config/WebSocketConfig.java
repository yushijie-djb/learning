package com.yushijie.common.core.messagebus.websocket.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import cn.keptdata.one2data.core.messagebus.websocket.common.WsConst;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket 配置
 * @date 2021/7/24 11:28
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private WsPrincipalHandshakeHandler wsPrincipalHandshakeHandler;

    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;

    @Autowired
    private AuthWebSocketHandlerDecoratorFactory authWebSocketHandlerDecoratorFactory;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 注册 Stomp的端点
         *
         * addEndpoint：添加STOMP协议的端点。这个HTTP URL是供WebSocket或SockJS客户端访问的地址 withSockJS：指定端点使用SockJS协议
         */
        registry.addEndpoint(WsConst.WS_ENDPOINT).setAllowedOriginPatterns("*")
            .addInterceptors(authHandshakeInterceptor).setHandshakeHandler(wsPrincipalHandshakeHandler).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 服务端发送消息给客户端的域,多个用逗号隔开
        registry.enableSimpleBroker(WsConst.WS_TOPIC_PATH, WsConst.WS_P2P_PATH);
        // 定义一对一推送的时候前缀
        registry.setUserDestinationPrefix(WsConst.WS_P2P_PATH);
        // 客户端发给Server 端的前缀
        registry.setApplicationDestinationPrefixes(WsConst.WS_CLIENT_PUSH_PATH);
    }

    /**
     * 这时实际spring websocket集群的新增的配置，用于获取建立websocket时获取对应的sessionId值
     *
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(authWebSocketHandlerDecoratorFactory);
        super.configureWebSocketTransport(registration);
    }

    /**
     * @description: 配置消息转换器
     * @author: HuangBing
     * @date: 2024/5/11 上午10:30
     * @param messageConverters
     * @return: boolean
     **/
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        GsonMessageConverter converter = new GsonMessageConverter();
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return true;
    }
}
