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

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.keptdata.one2data.core.messagebus.mq.base.BaseMessageReceiver;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.header.message.base.ReqMessage;

/**
 * @author Huangbing
 * @version 1.0
 * @description 单节点消息接收处理，处理单节点请求和响应消息。
 * @date: 2025/8/12 19:04
 */
@Component
public class SingleNodeIPCMessageReceiver extends BaseMessageReceiver {

    /**
     * @description: 接收单节点异步消息相应结果-指定节点响应
     * @author: HuangBing
     **/
    @RabbitListener(concurrency = "3",
        bindings = @QueueBinding(value = @Queue(value = "one2data.web.ipc.#{@currentNodeUuidKey}", durable = "true"),
            exchange = @Exchange(value = MessageBusConst.WEB_IPC_EXCHANGE),
            key = "one2data.web.ipc.#{@currentNodeUuidKey}"))
    public ReplyMessage receiveNodeIpcReqMessage(ReqMessage oneMessage, Message message, Channel channel) {
        return this.commonProcess(oneMessage, message, channel);
    }

    /**
     * @description: 接收单节点异步消息相应结果-任意节点响应
     * @author: HuangBing
     **/
    @RabbitListener(concurrency = "3",
        bindings = @QueueBinding(value = @Queue(value = MessageBusConst.WEB_IPC_COMMON_QUEUE, durable = "true"),
            exchange = @Exchange(value = MessageBusConst.WEB_IPC_EXCHANGE), key = MessageBusConst.WEB_IPC_COMMON_QUEUE))
    public ReplyMessage receiveCommonIpcReqMessage(ReqMessage oneMessage, Message message, Channel channel) {
        return this.commonProcess(oneMessage, message, channel);
    }
}
