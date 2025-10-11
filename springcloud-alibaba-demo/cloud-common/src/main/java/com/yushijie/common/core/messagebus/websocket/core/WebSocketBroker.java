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

package com.yushijie.common.core.messagebus.websocket.core;

import java.util.List;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.mq.base.BaseMessageReceiver;
import cn.keptdata.one2data.core.messagebus.websocket.common.PushMessage;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsConst;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsHelper;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket Server 端
 * @date 2021/7/21 23:35
 */
@Component
public class WebSocketBroker extends BaseMessageReceiver {

    private static final Log log = LogFactory.getLog();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WsHelper wsHelper;

    /**
     * @return void
     * @author Bob.Yang
     * @description 订阅WEBSocket 消息,并转发WebSocket
     * @date 2021/7/21 23:37
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false"),
        exchange = @Exchange(value = WsConst.WS_FANOUT_MQ_EXCHANGE, type = ExchangeTypes.FANOUT)))
    public void subscribe(PushMessage pushMessage, Message message, Channel channel) {

        try {
            String pushMessageTopic = pushMessage.getTopic();

            // 本机存在ws 连接，则进行发送,否则直接退出
            if (wsHelper.isExistGlobalConnection()) {

                // 广播发送消息
                if (wsHelper.hasSubscribe(pushMessageTopic,pushMessage.getReceiverUuids())) {

                    boolean isBroadCastMessage = pushMessage.isBroadcastMessage();
                    if (isBroadCastMessage) {
                        // 广播发送
                        simpMessagingTemplate.convertAndSend(WsConst.WS_TOPIC_PATH + "/" + pushMessageTopic,
                            pushMessage);
                    } else {
                        // 发送给特定的用户
                        List<String> receiverUuids = pushMessage.getReceiverUuids();
                        for (String receiverUuid : receiverUuids) {
                            simpMessagingTemplate.convertAndSendToUser(receiverUuid,
                                WsConst.WS_TOPIC_PATH + "/" + pushMessageTopic, pushMessage);
                        }

                    }

                }

            }

            // 手动Ack
            this.basicAck(message, channel);
        } catch (Exception e) {
            log.error("WEB|ws Receive broadCast And Handle Fail,MSG:{},Reason:{}", pushMessage, e);
            this.basicNack(message, channel);
        }

    }

}
