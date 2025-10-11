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

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description RabbitMQ 回调服务
 * @date 2021/6/1 14:52
 */
@Component
public class RabbitMQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static final Log log = LogFactory.getLog();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * @param correlationData
     * @param ack
     * @param cause
     * @return void
     * @author Bob.Yang
     * @description 确认消息是否成功到达MQ 的 Exchange
     * @date 2021/6/1 16:32
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        if (!ack) {
            log.error("WEB|MQ 消息发送至MQ 失败,详细信息:correlationData:{} , cause:{}", correlationData, cause);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("WEB|MQ 消息已成功发送至Exchange correlationData:{}", correlationData);
            }
        }

    }

    /**
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     * @return void
     * @author Bob.Yang
     * @description 消息从 Exchange 到 Queues 路由失败，触发该回调
     * @date 2021/6/1 16:33
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("WEB|MQ 消息退回! message:{} ,replyCode:{} ,replyText:{} ,exchange:{} ,routingKey:{}", message, replyCode,
            replyText, exchange, routingKey);
    }
}