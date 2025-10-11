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

package com.yushijie.common.core.messagebus.mq.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.core.Message;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Channel;

import cn.keptdata.one2data.core.handle.messagehandler.common.MessageHandlerKeeper;
import cn.keptdata.one2data.core.handle.messagehandler.common.SyncMessageHandler;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.core.MessageBusHelper;
import cn.keptdata.one2data.header.message.base.OneMessage;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.header.message.base.ReqMessage;
import cn.keptdata.one2data.header.message.protobuf.auth.Auth;
import cn.keptdata.one2data.header.message.protobuf.client.ClientMessage;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.pe.RecoveryMessage;
import cn.keptdata.one2data.util.common.JSONUtil;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description MQ消息接收基础类
 * @date 2021/4/1 20:22
 */
public abstract class BaseMessageReceiver {

    public static final Log log = LogFactory.getLog();

    private static final List<String> NEED_IGNORE_BODY_TYPES =
        new ArrayList<>(Arrays.asList(ClientMessage.BackupProgress.getDescriptor().getFullName(),
            RecoveryMessage.RecoveryProgress.getDescriptor().getFullName(),
            Auth.CorrectNodeLicense.getDescriptor().getFullName(),
            Auth.CorrectNodeLicenseAck.getDescriptor().getFullName(),
            ClientMessage.BackupStateReport.getDescriptor().getFullName(),
            ClientMessage.BackupWorkStateReport.getDescriptor().getFullName(),
            ClientMessage.SyncIndexInfoReport.getDescriptor().getFullName()));

    /**
     * @param message
     * @param channel
     * @return void
     * @author Bob.Yang
     * @description 若开启手动确认，消息处理器对接收到的消息进行手动确认
     * @date 2021/4/1 20:44
     */
    public void basicAck(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * @param message
     * @param channel
     * @return void
     * @author Bob.Yang
     * @description ack返回false，requeue:false 丢弃当前消息,消息无需重新回到队列
     * @date 2022-08-23 21:08
     */
    public void basicNack(Message message, Channel channel) {
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } catch (Exception e) {
            log.error("MQ Channel.BasicNack Error", e);
        }
    }

    /**
     * @param message
     * @param channel
     * @param requeue 是否需要重回队列
     * @return void
     * @author Bob.Yang
     * @description ack返回false，requeue:false并重新回到队列
     * @date 2022-08-23 21:08
     */
    public void basicNack(Message message, Channel channel, boolean requeue) {
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, requeue);
        } catch (Exception e) {
            log.error("MQ Channel.BasicNack Error", e);
        }
    }

    /**
     * @param oneMessage
     * @param clazz
     * @return T
     * @author Bob.Yang
     * @description 获取通用消息体内的Body 信息
     * @date 2021/4/17 20:51
     */
    public <T extends com.google.protobuf.Message> T getBody(Common.Message oneMessage, Class<T> clazz)
        throws InvalidProtocolBufferException {
        /*  String correlationId = oneMessage.getHeader().getCorrelationId();
        String bodyTypeDescriptor = oneMessage.getHeader().getBodyTypeDescriptor();
        
        if (!NEED_IGNORE_BODY_TYPES.contains(bodyTypeDescriptor)) {
            RedisTemplate redisTemplate = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);
            Boolean absent = redisTemplate.opsForValue().setIfAbsent(RedisKeyPrefix.ONE_MESSAGE.getKeyName(bodyTypeDescriptor, correlationId), 1, 10L, TimeUnit.MINUTES);
            if (!absent) {
                throw new OneRuntimeException("repeat msg");
            }
        }*/

        return oneMessage.getBody().unpack(clazz);
    }

    /**
     * @param oneMessage
     * @param clazz
     * @return T
     * @author Bob.Yang
     * @description
     * @date 2021/8/26 20:37
     */
    public static <T, M extends OneMessage> T getBody(M oneMessage, Class<T> clazz) {
        Object body = oneMessage.getBody();

        if (null == body) {
            return null;
        }
        if (clazz == String.class) {
            return (T)body;
        }
        return JSONUtil.object2Bean(body, clazz);
    }

    /**
     * @param ackMessage
     * @return boolean
     * @author Bob.Yang
     * @description 指定消息是否需要同步响应
     * @date 2021/8/28 14:38
     */
    public boolean needSyncAck(Common.Message ackMessage) {
        return MessageBusHelper.needSyncAck(ackMessage);
    }

    /**
     * @param oneMessage
     * @param message
     * @param channel
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description 通用IPC 消息处理
     * @date 2021-12-21 15:50
     */
    public ReplyMessage commonProcess(ReqMessage oneMessage, Message message, Channel channel) {

        try {
            this.basicAck(message, channel);

            // 获取请求消息的消息处理器
            SyncMessageHandler syncMessageHandler = MessageHandlerKeeper.get(oneMessage.getClass().getName());

            // 处理消息并生成响应消息体
            return syncMessageHandler.handle(oneMessage);

        } catch (Exception e) {
            log.error("message handle error,ServiceID={}, MsgId={}, MsgBody={}, MsgCreateDateTime:{}", e,
                oneMessage.getServiceId(), oneMessage.getId(), oneMessage.getBody(), oneMessage.getCreateTime());
            // 构造通用错误消息反馈
            return null;
        }

    }

}
