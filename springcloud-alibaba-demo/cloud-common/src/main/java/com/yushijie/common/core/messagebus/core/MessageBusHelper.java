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

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cn.keptdata.cache.redis.service.RedisService;
import cn.keptdata.one2data.api.operation.ApplicationApi;
import cn.keptdata.one2data.core.common.RedisKeyPrefix;
import cn.keptdata.one2data.core.messagebus.MessageBus;
import cn.keptdata.one2data.core.properties.MessageBusProperties;
import cn.keptdata.one2data.header.message.protobuf.common.Common;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息总线辅助类
 * @date 2021/8/28 14:18
 */
@Component
public class MessageBusHelper {

    private static RedisService redisService;

    private static MessageBus messageBus;

    private static MessageBusProperties messageBusProperties;

    private static ApplicationApi applicationApi;
    /**
     * @param ackMessage
     * @return boolean
     * @author Bob.Yang
     * @description 指定消息是否需要同步响应
     * @date 2021/8/28 14:23
     */
    public static boolean needSyncAck(Common.Message ackMessage) {

        Common.Header header = ackMessage.getHeader();

        // 消息上下文唯一ID（关联请求消息和发送消息）
        String correlationId = header.getCorrelationId();

        // 若未设置同步响应标记,默认异步响应并处理
        String keyName = RedisKeyPrefix.MSG_NEED_SYNC_ACK_FLAG.getKeyName(correlationId);
        Object needSyncAckObj = redisService.get(keyName);
        return needSyncAckObj != null && (boolean)needSyncAckObj;
    }

    /**
     * @param reqMessage
     * @return void
     * @author Bob.Yang
     * @description 标记消息需要同步反馈标记
     * @date 2021/8/28 14:33
     */
    public static void setSyncAckFlag(Common.Message reqMessage) {

        long busTimeoutMillis = messageBus.getBusTimeoutMillis();
        setSyncAckFlag(reqMessage, busTimeoutMillis);
    }

    /**
     * @param reqMessage
     * @param syncAckFlagTTLMillis 同步请求标记超时时间（单位：毫秒）
     * @return void
     * @author Bob.Yang
     * @description 设计消息需要同步反馈标记
     * @date 2021/8/28 14:33
     */
    public static void setSyncAckFlag(Common.Message reqMessage, Long syncAckFlagTTLMillis) {

        Common.Header header = reqMessage.getHeader();

        // 消息上下文唯一ID（关联请求消息和发送消息）
        String correlationId = header.getCorrelationId();

        // 标记当前请求为同步请求
        String flagKey = RedisKeyPrefix.MSG_NEED_SYNC_ACK_FLAG.getKeyName(correlationId);
        String senderKey = RedisKeyPrefix.MSG_SENDER_UUID_KEY.getKeyName(correlationId);
        long ttlSeconds = TimeUnit.MILLISECONDS.toSeconds(syncAckFlagTTLMillis);
        redisService.set(flagKey, true, ttlSeconds);
        // 设置发送者唯一标识,用于接收响应时进行指定节点消费,过期时间为同步请求超时时间+1秒，防止获取到响应进行处理时，缓存的发送者已过期
        redisService.set(senderKey, applicationApi.getManagementNodeUuidKey(), ttlSeconds + 1);
    }

    /**
     * @param properties
     * @param protoMessage
     * @return boolean
     * @author Bob.Yang
     * @description 判断给定的消息是否忽略Debug
     * @date 2022-06-08 21:19
     */
    public static boolean isIgnoreForDebug(MessageProperties properties, Common.Message protoMessage) {

        // 交换级类型
        String receivedExchange = properties.getReceivedExchange();
        // 消息体类型
        String bodyTypeDescriptor = protoMessage.getHeader().getBodyTypeDescriptor();

        if (messageBusProperties.getIgnoreDebugExchanges().contains(receivedExchange)
            || messageBusProperties.getIgnoreDebugMessages().contains(bodyTypeDescriptor)) {
            return true;
        }

        return false;
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        MessageBusHelper.redisService = redisService;
    }

    @Autowired
    @Lazy
    public void setMessageBus(MessageBus messageBus) {
        MessageBusHelper.messageBus = messageBus;
    }

    @Autowired
    @Lazy
    public void setApplicationApi(ApplicationApi applicationApi) {
        MessageBusHelper.applicationApi = applicationApi;
    }

    @Autowired
    public void setMessageBusProperties(MessageBusProperties messageBusProperties) {
        MessageBusHelper.messageBusProperties = messageBusProperties;
    }

}
