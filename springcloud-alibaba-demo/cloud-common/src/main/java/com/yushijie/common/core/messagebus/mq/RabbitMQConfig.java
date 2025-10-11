/*
 * Copyright(c) 2020-2021 KEPTDATA Software Technology Co., Limited.All rights reserved.
 *
 * KEPTDATA Software Technology Co., Limited claims this computer program as an unpublished work. Claim of copyright
 * does not imply waiver of other rights.
 *
 * NOTICE OF PROPRIETARY RIGHTS
 *
 * This program is a confidential trade secret and the property of KEPTDATA Software Technology Co., Limited.Use,
 * examination, reproduction, disassembly, decompiling, transfer and or disclosure to others of all or any part of this
 * software program are strictly prohibited except by express written agreement with KEPTDATA Software Technology Co.,
 * Limited.
 */

package com.yushijie.common.core.messagebus.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.core.messagebus.mq.receiver.OneDynamicBackupMessageListener;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description RabbitMQ 配置
 * @date 2021/3/11 21:18
 */
@Component
@Configuration
public class RabbitMQConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private OneMessageConverter oneMessageConverter;

    @Autowired
    private OneDynamicBackupMessageListener oneDynamicBackupMessageListener;

    /**
     * @return org.springframework.amqp.support.converter.MessageConverter
     * @author Bob.Yang
     * @description 自定义MQ 消息收发类型转换 </br>
     *              支持：Protobuf Message 类型消息转换 支持：普通Object 类型转换
     * @date 2021/3/11 21:19
     */
    @Bean
    public MessageConverter messageConverter() {
        return oneMessageConverter;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean("oneDynamicBackupMessageListenerContainer")
    public DirectMessageListenerContainer directMessageListenerContainer() {

        DirectMessageListenerContainer directMessageListenerContainer =
            new DirectMessageListenerContainer(connectionFactory);

        directMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        directMessageListenerContainer.setMessageListener(oneDynamicBackupMessageListener);

        return directMessageListenerContainer;
    }

}
