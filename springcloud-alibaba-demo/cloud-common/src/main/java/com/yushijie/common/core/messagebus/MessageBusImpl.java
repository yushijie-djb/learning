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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.yomahub.liteflow.util.JsonUtil;

import cn.keptdata.one2data.api.operation.ApplicationApi;
import cn.keptdata.one2data.core.common.CommonI18NCode;
import cn.keptdata.one2data.core.configuration.GenericOneConfig;
import cn.keptdata.one2data.core.delaytask.strategy.CommonDelayTaskStrategyFactory;
import cn.keptdata.one2data.core.i18n.info.FailInfo;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.config.BusQueueBind;
import cn.keptdata.one2data.core.messagebus.core.MessageBusConst;
import cn.keptdata.one2data.core.messagebus.core.MessageBusHelper;
import cn.keptdata.one2data.core.messagebus.core.MultiNodeIPCMessageCoordinator;
import cn.keptdata.one2data.core.messagebus.delaymessage.DelayMessage;
import cn.keptdata.one2data.core.messagebus.exception.ReceiveMsgTimeoutException;
import cn.keptdata.one2data.core.messagebus.future.CommonMessageFuture;
import cn.keptdata.one2data.core.messagebus.future.MultiNodeReplyMessageFuture;
import cn.keptdata.one2data.core.messagebus.mq.OneMessageConverter;
import cn.keptdata.one2data.core.messagebus.websocket.common.PushMessage;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsConst;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsHelper;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsTopic;
import cn.keptdata.one2data.core.properties.MessageBusProperties;
import cn.keptdata.one2data.header.configuration.dto.system.WebMessageBusTimeoutDTO;
import cn.keptdata.one2data.header.configuration.enumeration.GenericOneConfigEnum;
import cn.keptdata.one2data.header.message.base.NeedReplyMessage;
import cn.keptdata.one2data.header.message.base.OneMessage;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.helper.ProtoBufHelper;
import cn.keptdata.one2data.header.operation.delaytask.constants.DelayTaskConst;
import cn.keptdata.one2data.header.operation.delaytask.enitity.DelayTask;
import cn.keptdata.one2data.header.operation.delaytask.enumeration.DelayTaskStateEnum;
import cn.keptdata.one2data.util.collection.CollectionUtils;
import cn.keptdata.one2data.util.uuid.UUIDUtil;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息总线实现, 基于RabbitTemplate 进行简单封装
 * @date 2021/3/11 18:17
 */
@Component
public class MessageBusImpl implements MessageBus {

    private static final Log log = LogFactory.getLog();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private MessageBusProperties messageBusProperties;

    @Autowired
    private WsHelper wsHelper;

    @Autowired
    private DirectMessageListenerContainer oneDynamicBackupMessageListenerContainer;

    @Resource
    private OneMessageConverter oneMessageConverter;
    @Resource
    private ApplicationApi applicationApi;
    @Resource
    private String currentNodeUuidKey;
    @Override
    public <T extends OneMessage> void send(String queueName, T oneMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queue:{},MQ correlation:{},Message:{}", queueName, correlationData,
                oneMessage);
        }

        rabbitTemplate.convertAndSend(queueName, oneMessage, correlationData);
    }
    @Override
    public <T extends OneMessage> void send(String queueName, T oneMessage,long ttl) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queue:{},MQ correlation:{},Message:{}", queueName, correlationData,
                    oneMessage);
        }

        rabbitTemplate.send(queueName, convertTtlMessageIfNecessary(oneMessage,ttl), correlationData);
    }

    @Override
    public <T extends OneMessage> void sendWithDefaultTtl(String queueName, T oneMessage) {
        send(queueName, oneMessage, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, oneMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, oneMessage, correlationData);
    }

    @Override
    public <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage, long ttl) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                    routingKey, correlationData, oneMessage);
        }
        rabbitTemplate.send(exchangeName, routingKey, convertTtlMessageIfNecessary(oneMessage,ttl), correlationData);
    }

    @Override
    public <T extends OneMessage> void sendWithDefaultTtl(String exchangeName, String routingKey, T oneMessage) {
        send(exchangeName, routingKey, oneMessage, messageBusProperties.getTtlMillis());
    }


    @Override
    public <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage,
        MessagePostProcessor messagePostProcessor) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, oneMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, oneMessage, messagePostProcessor, correlationData);
    }

    @Override
    public <T extends DelayMessage> void commonDelaySend(T delayMessage) {
        this.delaySend(DelayTaskConst.EXCHANGE, DelayTaskConst.COMMON_DELAY_ROUTING_KEY, delayMessage);
    }

    @Override
    public <T extends DelayMessage> void delaySend(String routingKey, T delayMessage) {
        this.delaySend(DelayTaskConst.EXCHANGE, routingKey, delayMessage);
    }

    @Override
    public <T extends DelayMessage> void delaySend(String delayExchange, String routingKey, T delayMessage) {
        //校验延迟任务类型是否存在
        CommonDelayTaskStrategyFactory.getStrategy(delayMessage.getType());
        int delaySeconds = delayMessage.getDelaySeconds();
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", delayExchange,
                    routingKey, correlationData, delayMessage);
        }
        rabbitTemplate.send(delayExchange, routingKey, convertDelayMessageIfNecessary(delayMessage,delaySeconds), correlationData);
        this.saveDelayTaskInfoByMessage(delayMessage);
    }

    /**
     * @description: 保存延迟任务信息到数据库
     * @author: HuangBing
     * @date: 2024/12/12 19:26
     * @param delayMessage
     * @return: void
     **/
    private <T extends DelayMessage> void saveDelayTaskInfoByMessage(T delayMessage) {
        DelayTask delayTask = new DelayTask();
        delayTask.setUuid(delayMessage.getId());
        delayTask.setResourceUuid(delayMessage.getResourceUuid());
        delayTask.setResourceType(delayMessage.getResourceType());
        delayTask.setContext(JsonUtil.toJsonString(delayMessage));
        delayTask.setStatus(DelayTaskStateEnum.WAITING.getCode());
        delayTask.setDescription(DelayTaskStateEnum.WAITING.getDesc());
        delayTask.setCreateTime(new Date());
        applicationApi.insertSelectiveDelayTask(delayTask);
    }

    @Override
    public <T extends OneMessage> void send(String exchangeName, String routingKey, T oneMessage, MessagePostProcessor messagePostProcessor, long ttl) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                    routingKey, correlationData, oneMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, convertTtlMessageIfNecessary(oneMessage,ttl), messagePostProcessor, correlationData);
    }

    @Override
    public <T extends OneMessage> void sendWithDefaultTtl(String exchangeName, String routingKey, T oneMessage, MessagePostProcessor messagePostProcessor) {
        send(exchangeName, routingKey, oneMessage, messagePostProcessor, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Message> void send(String queueName, T protoBufMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queueName:{},MQ correlation:{},Message:{}", queueName, correlationData,
                protoBufMessage);
        }

        rabbitTemplate.convertAndSend(queueName, protoBufMessage, correlationData);
    }

    @Override
    public <T extends Message> void send(String queueName, T protoBufMessage, long ttl) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queueName:{},MQ correlation:{},Message:{}", queueName, correlationData,
                    protoBufMessage);
        }

        rabbitTemplate.send(queueName, convertTtlMessageIfNecessary(protoBufMessage,ttl), correlationData);
    }

    @Override
    public <T extends Message> void sendWithDefaultTtl(String queueName, T protoBufMessage) {
        send(queueName, protoBufMessage, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, protoBufMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, protoBufMessage, correlationData);
    }

    @Override
    public <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage, long ttl) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                    routingKey, correlationData, protoBufMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, convertTtlMessageIfNecessary(protoBufMessage,ttl), correlationData);
    }

    @Override
    public <T extends Message> void sendWithDefaultTtl(String exchangeName, String routingKey, T protoBufMessage) {
        send(exchangeName, routingKey, protoBufMessage, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage,
        MessagePostProcessor messagePostProcessor) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, protoBufMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, protoBufMessage, messagePostProcessor, correlationData);
    }

    @Override
    public <T extends Message> void send(String exchangeName, String routingKey, T protoBufMessage, MessagePostProcessor messagePostProcessor, long ttl) {
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                    routingKey, correlationData, protoBufMessage);
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, convertTtlMessageIfNecessary(protoBufMessage,ttl), messagePostProcessor, correlationData);
    }

    @Override
    public <T extends Message> void sendWithDefaultTtl(String exchangeName, String routingKey, T protoBufMessage, MessagePostProcessor messagePostProcessor) {
        send(exchangeName, routingKey, protoBufMessage, messagePostProcessor, messageBusProperties.getTtlMillis());
    }

    @Override
    public Common.Message syncSend(String exchangeName, String routingKey, Common.Message request)
        throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, null,0L);
    }

    @Override
    public Common.Message syncSendWithTtl(String exchangeName, String routingKey, Common.Message request, long ttl) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, null, ttl);
    }

    @Override
    public Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, null,messageBusProperties.getTtlMillis());
    }

    @Override
    public Common.Message syncSend(String exchangeName, String routingKey, Common.Message request,
        MessagePostProcessor messagePostProcessor,long ttl) throws ReceiveMsgTimeoutException {

        // 获取系统默认的消息通讯超时时间
        long defaultTimeoutMillis = this.getBusTimeoutMillis();

        return this.syncSend(exchangeName, routingKey, request, defaultTimeoutMillis, messagePostProcessor,ttl);
    }

    @Override
    public Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request, MessagePostProcessor messagePostProcessor) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, messagePostProcessor,messageBusProperties.getTtlMillis());
    }

    @Override
    public Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis)
        throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, timeoutMillis, null, timeoutMillis);
    }

    @Override
    public Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis, long ttl) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, timeoutMillis, null,ttl);
    }

    @Override
    public Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request, long timeoutMillis) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, timeoutMillis, null,messageBusProperties.getTtlMillis());
    }

    @Override
    public Common.Message syncSend(String exchangeName, String routingKey, Common.Message request, long timeoutMillis,
        MessagePostProcessor messagePostProcessor,long ttl) throws ReceiveMsgTimeoutException {

        // 构造MessageFuture
        CommonMessageFuture commonMessageFuture = new CommonMessageFuture(request, timeoutMillis);

        // 标记当前消息需要同步等待响应结果
        MessageBusHelper.setSyncAckFlag(request, timeoutMillis);

        // 异步发送消息
        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, request);
        }

        if (Objects.isNull(messagePostProcessor)) {
            rabbitTemplate.send(exchangeName, routingKey, convertTtlMessageIfNecessary(request,ttl), correlationData);
        } else {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, convertTtlMessageIfNecessary(request,ttl), messagePostProcessor, correlationData);
        }

        // 阻塞获取响应消息
        Common.Message replyMsg = commonMessageFuture.getReply();

        // 消息获取超时抛出检查异常
        if (ProtoBufHelper.isTimeoutReply(replyMsg)) {
            throw new ReceiveMsgTimeoutException(new FailInfo(CommonI18NCode.FAIL.RECEIVE_MSG_TIMEOUT));
        }

        return replyMsg;
    }

    @Override
    public Common.Message syncSendWithDefaultTtl(String exchangeName, String routingKey, Common.Message request, long timeoutMillis, MessagePostProcessor messagePostProcessor) throws ReceiveMsgTimeoutException {
        return this.syncSend(exchangeName, routingKey, request, timeoutMillis, messagePostProcessor,messageBusProperties.getTtlMillis());
    }

    @Override
    public ReplyMessage call(String queueName, NeedReplyMessage msg) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queueName:{},MQ correlation:{},Message:{}", queueName, correlationData, msg);
        }

        return (ReplyMessage)rabbitTemplate.convertSendAndReceive(queueName, msg, correlationData);
    }

    @Override
    public ReplyMessage call(NeedReplyMessage msg) {
        return call(msg, this.getBusTimeoutMillis());
    }

    @Override
    public ReplyMessage call(NeedReplyMessage msg, Long timeoutMillis) {

        CorrelationData correlationData = makeCorrelationData();
        msg.setRequestId(correlationData.getId());
        msg.setServiceId(currentNodeUuidKey);
        // WEB 内部IPC 固定名称
        String exchangeName = BusQueueBind.webIPC.getExchangeName();

        // 指定通用ipc消费队列
        String bindingKey = MessageBusConst.WEB_IPC_COMMON_QUEUE;

        return call(exchangeName, bindingKey, msg, timeoutMillis);
    }

    @Override
    public Map<String, ReplyMessage> callMultiNode(NeedReplyMessage msg) {
        return callMultiNode(msg, this.getBusTimeoutMillis());
    }

    @Override
    public Map<String, ReplyMessage> callMultiNode(NeedReplyMessage request, Long timeoutMillis) {
        CorrelationData correlationData = makeCorrelationData();
        // 构造MessageFuture
        request.setRequestId(correlationData.getId());
        request.setServiceId(currentNodeUuidKey);
        MultiNodeReplyMessageFuture multiNodeReplyMessageFuture =
            new MultiNodeReplyMessageFuture(request, timeoutMillis);

        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},,MQ correlation:{},Message:{}",
                MultiNodeIPCMessageCoordinator.FANOUT_EXCHANGE_MULTINODE_REQ, correlationData, request);
        }
        // 异步发送消息
        rabbitTemplate.send(MultiNodeIPCMessageCoordinator.FANOUT_EXCHANGE_MULTINODE_REQ, "",
            convertTtlMessageIfNecessary(request, timeoutMillis), correlationData);
        // 阻塞获取响应消息
        Map<String, ReplyMessage> reply = multiNodeReplyMessageFuture.getReply();

        // 消息获取超时抛出检查异常
        if (CollectionUtils.isEmpty(reply)) {
            throw new ReceiveMsgTimeoutException(new FailInfo(CommonI18NCode.FAIL.RECEIVE_MSG_TIMEOUT));
        }
        return reply;
    }

    @Override
    public ReplyMessage callNode(String nodeUuidKey, NeedReplyMessage msg) {
        return callNode(nodeUuidKey, msg, this.getBusTimeoutMillis());
    }

    @Override
    public ReplyMessage callNode(String nodeUuidKey, NeedReplyMessage request, Long timeoutMillis) {
        CorrelationData correlationData = makeCorrelationData();
        // 构造MessageFuture
        request.setRequestId(correlationData.getId());
        request.setServiceId(currentNodeUuidKey);
        // WEB 内部IPC 固定名称
        String exchangeName = BusQueueBind.webIPC.getExchangeName();
        // 根据节点标识 动态构造BindingKey
        String bindingKey = BusQueueBind.webIPC.getBindingKey(nodeUuidKey);
        return call(exchangeName, bindingKey, request, timeoutMillis);
    }

    @Override
    public ReplyMessage call(String exchangeName, String routingKey, NeedReplyMessage msg) {
        return call(exchangeName, routingKey, msg, this.getBusTimeoutMillis());
    }

    @Override
    public ReplyMessage call(String exchangeName, String routingKey, NeedReplyMessage msg, Long timeoutMillis) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, msg);
        }
        rabbitTemplate.setReplyTimeout(timeoutMillis);
        long startTime = System.currentTimeMillis();
        ReplyMessage replyMessage = (ReplyMessage)rabbitTemplate.convertSendAndReceive(exchangeName, routingKey,
            convertTtlMessageIfNecessary(msg, timeoutMillis), correlationData);
        long costTime = System.currentTimeMillis() - startTime;
        if (costTime > timeoutMillis && replyMessage == null) {
            // 花费时间大于超时时间收到为空，认为超时
            throw new ReceiveMsgTimeoutException(new FailInfo(CommonI18NCode.FAIL.RECEIVE_MSG_TIMEOUT));
        }
        return replyMessage;
    }

    @Override
    public <T extends Message> T call(String queueName, T protoBufMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,queueName:{},MQ correlation:{},Message:{}", queueName, correlationData,
                protoBufMessage);
        }

        return (T)rabbitTemplate.convertSendAndReceive(queueName, protoBufMessage, correlationData);
    }

    @Override
    public <T extends Message> T call(String exchangeName, String routingKey, T protoBufMessage) {

        CorrelationData correlationData = makeCorrelationData();
        if (log.isTraceEnabled()) {
            log.trace("WEB|MQ send Message,exchangeName:{},routingKey:{},MQ correlation:{},Message:{}", exchangeName,
                routingKey, correlationData, protoBufMessage);
        }

        return (T)rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, protoBufMessage, correlationData);
    }

    /**
     * @return org.springframework.amqp.rabbit.connection.CorrelationData
     * @author Bob.Yang
     * @description 随机生成 关联数据
     * @date 2021/6/1 16:48
     */
    private CorrelationData makeCorrelationData() {
        return new CorrelationData(UUIDUtil.genUUID());
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody) {
        // 广播推送，实时性消息，默认带有过期时间
        this.pushWithDefaultTtl(wsTopic, pushMessageBody, Collections.emptyList());
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, long ttl) {
        // 广播推送
        this.push(wsTopic, pushMessageBody, Collections.emptyList(), ttl);
    }


    public <T extends Serializable> void pushWithDefaultTtl(WsTopic wsTopic, T pushMessageBody) {
        push(wsTopic, pushMessageBody, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Serializable> void push(String wsTopicStr, T pushMessageBody) {
        // 广播推送
        this.pushWithDefaultTtl(wsTopicStr, pushMessageBody);
    }

    @Override
    public <T extends Serializable> void push(String wsTopicStr, T pushMessageBody, long ttl) {
        // 广播推送
        this.push(wsTopicStr, pushMessageBody, Collections.emptyList(), ttl);
    }

    /**
     * @description: 广播推送消息，带有默认过期时间
     * @author: HuangBing
     * @date: 2025/1/10 14:09
     * @param wsTopicStr
     * @param pushMessageBody
     * @return: void
     **/
    public <T extends Serializable> void pushWithDefaultTtl(String wsTopicStr, T pushMessageBody) {
        push(wsTopicStr, pushMessageBody, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Serializable> void push(String wsTopicStr, T pushMessageBody, List<String> receiveUserUuidList) {
        /**
         * 从 websocket 连接列表中获取指定的用户
         */
        this.pushWithDefaultTtl(wsTopicStr, pushMessageBody, receiveUserUuidList);
    }

    @Override
    public <T extends Serializable> void push(String wsTopicStr, T pushMessageBody, List<String> receiveUserUuidList, long ttl) {
        /**
         * 从 websocket 连接列表中获取指定的用户
         */
        PushMessage pushMessage = new PushMessage();
        pushMessage.setBody(pushMessageBody);
        pushMessage.setReceiverUuids(receiveUserUuidList);
        this.pushMessage(wsTopicStr, pushMessage,ttl);
    }

    /**
     * @description: 广播推送消息，带有默认过期时间，推送给指定用户列表
     * @author: HuangBing
     * @date: 2025/1/10 14:02
     * @param wsTopicStr
     * @param pushMessageBody
     * @param receiveUserUuidList
     * @return: void
     **/
    public <T extends Serializable> void pushWithDefaultTtl(String wsTopicStr, T pushMessageBody, List<String> receiveUserUuidList) {
        push(wsTopicStr, pushMessageBody, receiveUserUuidList, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, List<String> receiveUserUuidList) {
        /**
         * 从 websocket 连接列表中获取指定的用户
         */
        this.pushWithDefaultTtl(wsTopic, pushMessageBody, receiveUserUuidList);
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, List<String> receiveUserUuidList, long ttl) {
        /**
         * 从 websocket 连接列表中获取指定的用户
         */
        PushMessage pushMessage = new PushMessage();
        pushMessage.setBody(pushMessageBody);
        pushMessage.setReceiverUuids(receiveUserUuidList);

        this.pushMessage(wsTopic.getTopic(), pushMessage,ttl);
    }

    /**
     * @description: 广播推送消息，带有默认过期时间，推送给指定用户列表
     * @author: HuangBing
     * @date: 2025/1/10 14:00
     * @param wsTopic
     * @param pushMessageBody
     * @param receiveUserUuidList
     * @return: void
     **/
    public <T extends Serializable> void pushWithDefaultTtl(WsTopic wsTopic, T pushMessageBody, List<String> receiveUserUuidList) {
        this.push(wsTopic, pushMessageBody, receiveUserUuidList, messageBusProperties.getTtlMillis());
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, String receiveUserUuid) {
        this.pushWithDefaultTtl(wsTopic, pushMessageBody, receiveUserUuid);
    }

    @Override
    public <T extends Serializable> void push(WsTopic wsTopic, T pushMessageBody, String receiveUserUuid, long ttl) {
        List<String> singleUsers = Collections.singletonList(receiveUserUuid);
        this.push(wsTopic, pushMessageBody, singleUsers, ttl);
    }

    /**
     * @description: 推送消息，带有默认过期时间，推送给指定用户
     * @author: HuangBing
     * @date: 2025/1/10 14:07
     * @param wsTopic
     * @param pushMessageBody
     * @param receiveUserUuid
     * @return: void
     **/
    public <T extends Serializable> void pushWithDefaultTtl(WsTopic wsTopic, T pushMessageBody, String receiveUserUuid) {
        this.push(wsTopic, pushMessageBody, receiveUserUuid, messageBusProperties.getTtlMillis());
    }

    @Override
    public long getBusTimeoutMillis() {
        // 系统配置文件的超时时间配置
        long timeoutMillis_prop = messageBusProperties.getTimeoutMillis();

        try {
            // 配置中心的配置
            WebMessageBusTimeoutDTO webMessageBusTimeoutDTO = GenericOneConfig.configDTO(GenericOneConfigEnum.WEB_MESSAGE_BUS_TIMEOUT, WebMessageBusTimeoutDTO.class);
            Integer timeoutSeconds = webMessageBusTimeoutDTO.getTimeout();
            if (Objects.nonNull(timeoutSeconds) && timeoutSeconds > 0) {
                return TimeUnit.SECONDS.toMillis(timeoutSeconds);
            }
        } catch (Exception e) {
            log.error("获取消息总线超时时间出现异常,已返回默认超时时间", e);
        }

        return timeoutMillis_prop;
    }

    @Override
    public void purgeQueue(String queueName) {

        if (log.isWarnEnabled()) {
            log.warn("AMQP PURGE QUEUE ({})", queueName);
        }

        rabbitAdmin.purgeQueue(queueName);
    }

    @Override
    public String addQueue(String queueName) {

        if (log.isDebugEnabled()) {
            log.debug("start add queue {}", queueName);
        }

        Queue queue = new Queue(queueName, true, false, false);
        queue.addArgument("x-single-active-consumer", true);

        return rabbitAdmin.declareQueue(queue);

    }

    @Override
    public void bindingQueue(String queueName, String exchangeName, String routingKey) {

        if (log.isDebugEnabled()) {
            log.debug("start binding queue {}", queueName);
        }

        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);

        rabbitAdmin.declareBinding(binding);

    }

    @Override
    public Boolean deleteQueue(String queueName) {
        if (log.isDebugEnabled()) {
            log.debug("start delete queue {}", queueName);
        }

        return rabbitAdmin.deleteQueue(queueName);

    }

    @Override
    public void addBackupQueueListener(String queueName) {
        if (log.isDebugEnabled()) {
            log.debug("start add listener queue {}", queueName);
        }

        oneDynamicBackupMessageListenerContainer.addQueueNames(queueName);
    }

    @Override
    public void removeBackupQueueListener(String queueName) {
        if (log.isDebugEnabled()) {
            log.debug("start remove listener queue {}", queueName);
        }

        oneDynamicBackupMessageListenerContainer.removeQueueNames(queueName);
    }

    @Override
    public QueueInformation getQueueInfo(String queueName) {
        return rabbitAdmin.getQueueInfo(queueName);
    }

    private void pushMessage(String wsTopicStr, PushMessage pushMessage,long ttl) {


        // 1. 全局不存在session 连接则不做处理
        if (!wsHelper.isExistGlobalConnection()) {
            return;
        }
        // 2.topic 未订阅则不做处理
        if (!wsHelper.hasSubscribe(wsTopicStr, pushMessage.getReceiverUuids())) {
            return;
        }
        pushMessage.setTopic(wsTopicStr);

        // 通过广播方式发送到各服务实例中
        this.send(WsConst.WS_FANOUT_MQ_EXCHANGE, "", pushMessage,ttl);
    }
    /**
     * @description: 自定义消息转换
     * @author: HuangBing
     * @date: 2024/11/20 11:18
     * @param object 消息对象
     * @param ttl 消息过期时间
     * @return: org.springframework.amqp.core.Message
     **/
    protected org.springframework.amqp.core.Message convertTtlMessageIfNecessary(Object object, Long ttl) {
        MessageProperties messageProperties = new MessageProperties();
        if (ttl > 0){
            // 设置消息过期时间
            messageProperties.setExpiration(String.valueOf(ttl));
        }
        return object instanceof org.springframework.amqp.core.Message ? (org.springframework.amqp.core.Message)object : oneMessageConverter.toMessage(object,messageProperties);
    }

    /**
     * @description: 自定义消息转换
     * @author: HuangBing
     * @date: 2024/11/20 11:18
     * @param object 消息对象
     * @param delaySeconds 延迟投递时间 单位秒
     * @return: org.springframework.amqp.core.Message
     **/
    protected org.springframework.amqp.core.Message convertDelayMessageIfNecessary(Object object, Integer delaySeconds) {
        MessageProperties messageProperties = new MessageProperties();
        if (delaySeconds==null||delaySeconds < 0|| delaySeconds>Integer.MAX_VALUE/1000){
            //延迟时间必须大于等于0并且小于等于2147483(int最大值/1000)
            throw new IllegalArgumentException("delaySeconds must be greater than or equal to 0 and less than or equal to 2147483");
        }
        //设置消息延迟时间
        messageProperties.setDelay(delaySeconds*1000);
        return object instanceof org.springframework.amqp.core.Message ? (org.springframework.amqp.core.Message)object : oneMessageConverter.toMessage(object,messageProperties);
    }

}
