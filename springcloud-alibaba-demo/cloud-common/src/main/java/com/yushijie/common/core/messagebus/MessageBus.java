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

package com.yushijie.common.core.messagebus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.QueueInformation;

import com.google.protobuf.Message;

import cn.keptdata.one2data.core.messagebus.delaymessage.DelayMessage;
import cn.keptdata.one2data.core.messagebus.exception.ReceiveMsgTimeoutException;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsTopic;
import cn.keptdata.one2data.header.message.base.NeedReplyMessage;
import cn.keptdata.one2data.header.message.base.OneMessage;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.header.message.protobuf.common.Common;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息总线，基于MQ 进行消息收发
 * @date 2021/3/11 11:48
 */
public interface MessageBus {

    /**
     * @param oneMessage
     * @param queueName
     * @return void
     * @author Bob.Yang
     * @description 发送消息请求
     * @date 2021/3/11 18:13
     */
    <T extends OneMessage> void send(String queueName, T oneMessage);
    /**
     * @description: 发送消息,指定队列名称,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:21
     * @param queueName 队列名称
     * @param oneMessage 消息体
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends OneMessage> void send(String queueName, T oneMessage, long ttl);
    /**
     * @param queueName
     * @param oneMessage
     * @return void
     * @description 发送消息,指定队列名称,使用默认TTL时间 3000ms,消息过期后会被丢弃,不会被消费者消费
     * @date: 2025/1/10 11:26
     */
    <T extends OneMessage> void sendWithDefaultTtl(String queueName, T oneMessage);
    /**
     * @param oneMessage
     * @param exchangeName 交换及名称，当为：publish/subscribe 模型下使用
     * @param routingKey 路由键
     * @return void
     * @author Bob.Yang
     * @description 发送消息请求
     * @date 2021/3/11 18:13
     */
    <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage);

    /**
     * @description: 发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:07
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param oneMessage 消息体
     * @return: void
     **/
    <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage, long ttl);
    /**
     * @description:  发送消息,指定交换机和路由键,使用默认TTL时间 3000ms,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName
     * @param routingKey
     * @param oneMessage
     * @return: void
     **/
    <T extends OneMessage> void sendWithDefaultTtl(String exchangeName, String routingKey, T oneMessage);
    /**
     * @param exchangeName
     * @param routingKey
     * @param oneMessage
     * @param messagePostProcessor
     * @return void
     * @author yushijie
     * @description 发送消息请求
     * @date 2023/3/7 14:15
     */
    <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage,
        MessagePostProcessor messagePostProcessor);
    /**
     * @description: 发送延迟消息,指定队列名称,并且指定延迟时间,单位秒,到达设定的延迟消息后，消息被投递到指定队列进行消费
     * @author: HuangBing
     * @date: 2024/12/12 11:47
     * （交换机 固定为 one2data.web.delayTask）
     * （队列固定为 one2data.web.delayTask.common）
     * @param delayMessage 延迟消息体
     * <a href="https://github.com/rabbitmq/rabbitmq-delayed-message-exchange">关于最大支持的延迟时间的参考链接</a>
     * @return: void
     **/
    <T extends DelayMessage> void commonDelaySend(T delayMessage);
    /**
     * @description: 发送延迟消息,指定队列名称,并且指定延迟时间,单位秒,到达设定的延迟消息后，消息被投递到指定队列进行消费
     * @author: HuangBing
     * @date: 2024/12/12 11:47
     * @param routingKey 队列路由键（交换机 固定为 one2data.web.delayTask）
     * @param delayMessage 延迟消息体
     * <a href="https://github.com/rabbitmq/rabbitmq-delayed-message-exchange">关于最大支持的延迟时间的参考链接</a>
     * @return: void
     **/
    <T extends DelayMessage> void delaySend(String routingKey, T delayMessage);


    /**
     * @description: 发送延迟消息, 指定队列名称, 并且指定延迟时间, 单位秒, 到达设定的延迟消息后，消息被投递到指定队列进行消费
     * @author: HuangBing
     * @date: 2024/12/12 11:47
     * @param routingKey 路由键
     * @param delayExchange 自定义延迟交换机
     * @param delayMessage 延迟消息体
     *
     * @return: void
     **/
    <T extends DelayMessage> void delaySend(String delayExchange,String routingKey,T delayMessage);
    /**
     * @description: 发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:22
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param oneMessage 消息体
     * @param messagePostProcessor 消息后置处理器
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage,
                                     MessagePostProcessor messagePostProcessor,long ttl);
    /**
     * @description: 发送消息,指定交换机和路由键,并且指定默认的TTL时间,单位ms,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param oneMessage 消息体
     * @param messagePostProcessor 消息后置处理器
     * @return: void
     **/
    <T extends OneMessage> void sendWithDefaultTtl(String exchangeName, String routingKey, T oneMessage,
                                     MessagePostProcessor messagePostProcessor);
    /**
     * @param queueName
     * @param protoBufMessage
     * @return void
     * @author Bob.Yang
     * @description 发送ProtoBuf 类型的消息
     * @date 2021/3/11 15:59
     */
    <T extends Message> void send(String queueName, T protoBufMessage);

    /**
     * @description: 发送消息,指定队列名称,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:26
     * @param queueName 队列名称
     * @param protoBufMessage 消息体
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Message> void send(String queueName, T protoBufMessage, long ttl);
    /**
     * @description: 发送消息,指定队列名称,并且指定默认的TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param queueName 队列名称
     * @param protoBufMessage 消息体
     * @return: void
     **/
    <T extends Message> void sendWithDefaultTtl(String queueName, T protoBufMessage);
    /**
     * @param protoBufMessage
     * @return void
     * @author Bob.Yang
     * @description 发送ProtoBuf 类型的消息
     * @date 2021/3/11 15:59
     */
    <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage);

    /**
     * @description: 发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:28
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param protoBufMessage 消息体
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage,long ttl);

    /**
     * @description: 发送消息,指定交换机和路由键,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param protoBufMessage 消息体
     * @return: void
     **/
    <T extends Message> void sendWithDefaultTtl(String exchangeName, String routingKey, T protoBufMessage);
    /**
     * @param protoBufMessage
     * @param exchangeName
     * @param routingKey
     * @param messagePostProcessor
     * @return void
     * @author Bob.Yang
     * @description 发送ProtoBuf 类型的消息
     * @date 2021/3/11 15:59
     */
    <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage,
        MessagePostProcessor messagePostProcessor);

    /**
     * @description: 发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:32
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param protoBufMessage 消息体
     * @param messagePostProcessor 消息后置处理器
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage,
                                  MessagePostProcessor messagePostProcessor,long ttl);
    /**
     * @description: 发送消息,指定交换机和路由键,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param protoBufMessage 消息体
     * @param messagePostProcessor 消息后置处理器
     * @return: void
     **/
    <T extends Message> void sendWithDefaultTtl(String exchangeName, String routingKey, T protoBufMessage,
                                  MessagePostProcessor messagePostProcessor);
    /**
     * @param exchangeName
     * @param routingKey
     * @param request
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     * @author Bob.Yang
     * @description 同步发送消息
     * @date 2021/7/1 18:19
     */
    Common.Message syncSend(String exchangeName, String routingKey, Common.Message request)
        throws ReceiveMsgTimeoutException;

    /**
     * @description: 同步发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:35
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param ttl 消息过期时间
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSendWithTtl(String exchangeName, String routingKey, Common.Message request, long ttl) throws ReceiveMsgTimeoutException;

    /**
     * @description: 同步发送消息,指定交换机和路由键,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request) throws ReceiveMsgTimeoutException;

    /**
     * @param exchangeName
     * @param routingKey
     * @param request
     * @param timeoutMillis 超时时间 (单位:毫秒)
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     * @author Bob.Yang
     * @description 同步发送消息
     * @date 2021-11-16 17:07
     */
    Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis)
        throws ReceiveMsgTimeoutException;

    /**
     * @description: 同步发送消息,指定交换机和路由键,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 11:44
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param timeoutMillis 等待响应超时时间
     * @param ttl 消息过期时间
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis,long ttl)
            throws ReceiveMsgTimeoutException;

    /**
     * @description: 同步发送消息,指定交换机和路由键,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param timeoutMillis 等待响应超时时间
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request, long timeoutMillis)
            throws ReceiveMsgTimeoutException;
    /**
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param messagePostProcessor 消息后置处理器
     * @param ttl 消息过期时间
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     * @author Bob.Yang
     * @description 同步发送消息
     * @date 2021/7/1 18:19
     */
    Common.Message syncSend(String exchangeName, String routingKey, Common.Message request,
        MessagePostProcessor messagePostProcessor,long ttl) throws ReceiveMsgTimeoutException;

    /**
     * @description: 同步发送消息,指定交换机和路由键及后置处理器,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param messagePostProcessor 消息后置处理器
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request,
                            MessagePostProcessor messagePostProcessor) throws ReceiveMsgTimeoutException;

    /**
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param timeoutMillis 等待响应超时时间 (单位:毫秒)
     * @param messagePostProcessor 消息后置处理器
     * @param ttl 消息过期时间
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     * @author Bob.Yang
     * @description 同步发送消息
     * @date 2021-11-16 17:07
     */
    Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis,
        MessagePostProcessor messagePostProcessor,long ttl) throws ReceiveMsgTimeoutException;


    /**
     * @description: 同步发送消息,指定交换机和路由键及后置处理器以及等待回复超时时间,并且指定默认TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2025/1/10 11:26
     * @param exchangeName 交换机名称
     * @param routingKey 路由键
     * @param request 消息体
     * @param messagePostProcessor 消息后置处理器
     * @return: cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     **/
    Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request, long timeoutMillis,
                            MessagePostProcessor messagePostProcessor) throws ReceiveMsgTimeoutException;
    /**
     * @param msg
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description RPC 调用，发送请求并获取响应结果
     * @date 2021/3/11 16:08
     */
    ReplyMessage call(String queueName, NeedReplyMessage msg);

    /**
     * @param msg
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description 通用 WEB 内部RPC 调用,发送请求并获取响应结果--任意节点执行
     * @date 2021-12-20 19:01
     */
    ReplyMessage call(NeedReplyMessage msg);

    /**
     * @param msg
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description 通用 WEB 内部RPC 调用,发送请求并获取响应结果 并手动指定超时时间（ms）--任意节点执行
     * @date 2021-12-20 19:01
     */
    ReplyMessage call(NeedReplyMessage msg, Long timeoutMillis);

    /**
     * @description: 通用指定单节点调用，发送请求并获取响应结果，从配置中获取默认超时时间
     * @author: HuangBing
     * @date: 2025/8/12 11:03
     * @param msg 需要回复的消息请求
     * @return: cn.keptdata.one2data.header.message.base.ReplyMessage
     **/
    ReplyMessage callNode(String nodeUuidKey, NeedReplyMessage msg);

    /**
     * @description: 通用指定单节点调用，发送请求并获取响应结果 ,手动指定超时时间（ms）
     * @author: HuangBing
     * @date: 2025/8/12 11:17
     * @param msg
     * @param timeoutMillis
     * @return: cn.keptdata.one2data.header.message.base.ReplyMessage
     **/
    ReplyMessage callNode(String nodeUuidKey, NeedReplyMessage msg, Long timeoutMillis);

    /**
     * @description: 通用多节点调用，发送请求并获取响应结果，从配置中获取默认超时时间
     * @author: HuangBing
     * @date: 2025/8/12 11:03
     * @param msg 需要回复的消息请求
     * @return: java.util.Map<nodeUuidKey(String), cn.keptdata.one2data.header.message.base.ReplyMessage>
     **/
    Map<String, ReplyMessage> callMultiNode(NeedReplyMessage msg);

    /**
     * @description: 通用多节点调用，发送请求并获取响应结果 ,手动指定超时时间
     * @author: HuangBing
     * @date: 2025/8/12 11:17
     * @param msg
     * @param timeoutMillis
     * @return: java.util.Map<nodeUuidKey(String), cn.keptdata.one2data.header.message.base.ReplyMessage>
     **/
    Map<String, ReplyMessage> callMultiNode(NeedReplyMessage msg, Long timeoutMillis);

    /**
     * @param msg
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description RPC 调用，发送请求并获取响应结果
     * @date 2021/3/11 16:08
     */
    ReplyMessage call(String exchangeName, String routingKey, NeedReplyMessage msg);

    /**
     * @param msg
     * @return cn.keptdata.one2data.header.message.base.ReplyMessage
     * @author Bob.Yang
     * @description RPC 调用，发送请求并获取响应结果,并指定超时时间
     * @date 2021/3/11 16:08
     */
    ReplyMessage call(String exchangeName, String routingKey, NeedReplyMessage msg, Long timeoutMillis);
    /**
     * @param queueName
     * @param protoBufMessage
     * @return com.google.protobuf.Message
     * @author Bob.Yang
     * @description 发送ProtoBuf 类型的消息并获取
     * @date 2021/3/11 16:11
     */
    <T extends Message> T call(String queueName, T protoBufMessage);

    /**
     * @param exchangeName
     * @param routingKey
     * @param protoBufMessage
     * @return com.google.protobuf.Message
     * @author Bob.Yang
     * @description 发送ProtoBuf 类型的消息并获取
     * @date 2021/3/11 16:11
     */
    <T extends Message> T call(String exchangeName, String routingKey, T protoBufMessage);

    /**
     * @param wsTopic
     * @param pushMessageBody
     * @return void
     * @author Bob.Yang
     * @description 广播推送消息-推送给系统所有用户 （转发 至 RabbitMQ，实时性消息，默认带有过期时间）
     * @date 2021/7/24 15:47
     */
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody);

    /**
     * @description: 广播推送消息-推送给系统所有用户 （转发 至 RabbitMQ，实时性消息，默认带有过期时间）且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 14:14
     * @param wsTopic wsTopic
     * @param pushMessageBody 推送消息体
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody,long ttl);
    /**
     * @description: 广播推送消息-推送给系统所有用户 （转发 至 RabbitMQ，实时性消息，默认带有过期时间）
     * @author: HuangBing
     * @date: 2024/5/11 上午10:16
     * @param wsTopicStr topic字符串
     * @param pushMessageBody
     * @return: void
     **/
    <T extends Serializable> void push(String wsTopicStr, T pushMessageBody);

    /**
     * @description: 广播推送消息-推送给系统所有用户 （转发 至 RabbitMQ）且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 14:16
     * @param wsTopicStr topic字符串
     * @param pushMessageBody 推送消息体
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Serializable> void push(String wsTopicStr, T pushMessageBody,long ttl);
    /**
     * @description: 推送消息给特定的用户集合
     * @author: HuangBing
     * @date: 2024/5/11 上午10:23
     * @param wsTopicStr topic字符串
     * @param pushMessageBody 推送消息体
     * @param receiveUserUuidList 待接收该推送消息的用户，实时性消息，默认带有过期时间
     * @return: void
     **/
    <T extends Serializable> void push(String wsTopicStr, T pushMessageBody, List<String> receiveUserUuidList);

    /**
     * @description: 推送消息给特定的用户集合,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 14:23
     * @param wsTopicStr topic字符串
     * @param pushMessageBody  推送消息体
     * @param receiveUserUuidList 待接收该推送消息的用户
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Serializable> void push(String wsTopicStr, T pushMessageBody, List<String> receiveUserUuidList, long ttl);

    /**
     * @param wsTopic
     * @param pushMessageBody
     * @param receiveUserUuidList 待接收该推送消息的用户
     * @return void
     * @author Bob.Yang
     * @description 推送消息给特定的用户集合，实时性消息，默认带有过期时间
     * @date 2023-03-02 15:18
     */
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, List<String> receiveUserUuidList);

    /**
     * @description: 推送消息给特定的用户集合,并且指定TTL时间,单位毫秒,消息过期后会被丢弃,不会被消费者消费
     * @author: HuangBing
     * @date: 2024/11/20 14:23
     * @param wsTopic 主题枚举值
     * @param pushMessageBody  推送消息体
     * @param receiveUserUuidList 待接收该推送消息的用户
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, List<String> receiveUserUuidList, long ttl);

    /**
     * @param wsTopic
     * @param pushMessageBody
     * @param receiveUserUuid
     * @return void
     * @author Bob.Yang
     * @description 将消息推送给特定的用户，实时性消息，默认带有过期时间
     * @date 2023-03-02 15:23
     */
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, String receiveUserUuid);

    /**
     * @description: 将消息推送给特定的用户
     * @author: HuangBing
     * @date: 2024/11/20 14:24
     * @param wsTopic 主题枚举值
     * @param pushMessageBody 推送消息体
     * @param receiveUserUuid 接收用户UUID
     * @param ttl 消息过期时间
     * @return: void
     **/
    <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, String receiveUserUuid,long ttl);
    /**
     * @return long
     * @author Bob.Yang
     * @description 获取消息总线超时时间（单位：毫秒）
     * @date 2023-03-07 20:51
     */
    long getBusTimeoutMillis();

    /**
     * @author yushijie
     * @description 清空队列消息 返回被清除的消息数量
     * @date 2024/8/1 16:07
     * @param queueName
     * @return void
     */
    void purgeQueue(String queueName);

    /*
     * @author yushijie
     * @description 创建队列 返回队列名称
     * @date 2024/9/3 16:18
     * @param queueName
     */
    String addQueue(String queueName);

    /**
     * @author yushijie
     * @description 绑定队列与交换机
     * @date 2024/9/3 16:27
     * @param queueName
     * @param exchangeName
     * @param routingKey
     * @return void
     */
    void bindingQueue(String queueName, String exchangeName, String routingKey);

    /**
     * @author yushijie
     * @description 删除队列
     * @date 2024/9/3 17:50
     * @param queueName
     * @return void
     */
    Boolean deleteQueue(String queueName);

    /**
     * @author yushijie
     * @description
     * @date 2024/9/3 18:19
     * @param queueName
     * @return void
     */
    void addBackupQueueListener(String queueName);

    /**
     * @author yushijie
     * @description 移除备份队列监听
     * @date 2024/9/4 14:10
     * @param queueName
     * @return void
     */
    void removeBackupQueueListener(String queueName);

    /**
     * @author yushijie
     * @description 获取队列信息
     * @date 2024/9/10 16:14
     * @param queueName
     * @return org.springframework.amqp.core.QueueInformation
     */
    QueueInformation getQueueInfo(String queueName);

}
