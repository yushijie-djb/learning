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

package com.yushijie.common.core.messagebus.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.keptdata.cache.redis.service.RedisService;
import cn.keptdata.one2data.core.common.RedisKeyPrefix;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.MessageBus;
import cn.keptdata.one2data.core.messagebus.future.CommonMessageFuture;
import cn.keptdata.one2data.core.messagebus.mq.base.BaseMessageReceiver;
import cn.keptdata.one2data.header.message.protobuf.common.Common;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 异步消息协调处理器
 * @date 2021/7/1 14:50
 */
@Component
public class AsyncMessageCoordinator extends BaseMessageReceiver {

    private static final String DIRECT_EXCHANGE_ASYNC_2_SYNC = "one2data.web.ipc.async2Sync";

    private static final Log log = LogFactory.getLog();

    public static final Map<String, CommonMessageFuture> futureMap = new ConcurrentHashMap<>();
    @Resource
    private MessageBus messageBus;
    @Resource
    private RedisService redisService;

    /**
     * @description: 接收异步消息接收结果
     * @author: HuangBing
     * @date: 2025/7/16 15:03
     * @param oneMessage 异步消息
     * @param channel 信道
     * @return: void
     **/
    @RabbitListener(
        bindings = @QueueBinding(value = @Queue(value = "one2data.asyncAck.#{@currentNodeUuidKey}", durable = "true"),
            exchange = @Exchange(value = DIRECT_EXCHANGE_ASYNC_2_SYNC), key = "#{@currentNodeUuidKey}"))
    public void receiveNodeAsyncAckMessage(Common.Message oneMessage, Message message, Channel channel) {
        try {
            // 获取指定请求的响应信息
            CommonMessageFuture messageFuture = futureMap.get(oneMessage.getHeader().getCorrelationId());
            log.debug("receiveNodeAsyncAckMessage,requestId:{}", oneMessage.getHeader().getCorrelationId());
            if (messageFuture != null) {
                // 调用接收方法
                messageFuture.receive(oneMessage);
                log.debug("after receive,requestId:{}", oneMessage.getHeader().getCorrelationId());
            }

            // 手动ACK
            this.basicAck(message, channel);
        } catch (Throwable e) {
            log.error("handle AsyncAckMessage error", e);
            this.basicNack(message, channel);
        }

    }

    /**
     * @param oneMessage
     * @return void
     * @author Bob.Yang
     * @description 将收到的消息进行广播发送
     * @date 2021/7/1 18:15
     */
    public void broadcastCommonMessage(Common.Message oneMessage) {
        /**
         * 先从本机查找，若本机无上下文，则广播发送其他机器处理
         */
        // 获取指定请求的响应信息
        String correlationId = oneMessage.getHeader().getCorrelationId();
        CommonMessageFuture messageFuture = futureMap.get(correlationId);

        if (messageFuture != null) {
            // 调用接收方法
            messageFuture.receive(oneMessage);
            return;
        }
        String senderNode = redisService.get(RedisKeyPrefix.MSG_SENDER_UUID_KEY.getKeyName(correlationId));
        log.info("当前机器无法找到消息上下文{},准备转发到指定的消息发送者:{}进行处理", correlationId, senderNode);
        if (senderNode == null) {
            log.error("无法获取到消息发送者节点信息,请检查消息发送者是否被正确设置，correlationId：{}", correlationId);
            return;
        }
        messageBus.send(DIRECT_EXCHANGE_ASYNC_2_SYNC, senderNode, oneMessage);
    }

    public static void putFuture(CommonMessageFuture future) {
        futureMap.put(future.getRequestId(), future);
    }

    public static void removeFuture(String requestId) {
        futureMap.remove(requestId);
    }
}
