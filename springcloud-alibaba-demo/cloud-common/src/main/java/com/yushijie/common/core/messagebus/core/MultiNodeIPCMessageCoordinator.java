package com.yushijie.common.core.messagebus.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.MessageBus;
import cn.keptdata.one2data.core.messagebus.future.MultiNodeReplyMessageFuture;
import cn.keptdata.one2data.core.messagebus.mq.base.BaseMessageReceiver;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.header.message.base.ReqMessage;

/**
 * @description: 多节点消息协调器，处理多节点请求和响应消息。
 * @author: HuangBing
 * @date: 2025/8/12 19:04
 * @return: null
 **/
@Component
public class MultiNodeIPCMessageCoordinator extends BaseMessageReceiver {

    private static final String DIRECT_EXCHANGE_MULTINODE_REPLY = "one2data.web.ipc.multiNodeReply";
    public static final String FANOUT_EXCHANGE_MULTINODE_REQ = "one2data.web.ipc.multiNodeReq";
    private static final Log log = LogFactory.getLog();

    public static final Map<String, MultiNodeReplyMessageFuture> futureMap = new ConcurrentHashMap<>();
    @Resource
    private MessageBus messageBus;
    @Resource
    private String currentNodeUuidKey;

    /**
     * @description: 接收多节点异步消息相应结果
     * @author: HuangBing
     **/
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "one2data.web.ipc.multiNodeReply.#{@currentNodeUuidKey}", durable = "true"),
        exchange = @Exchange(value = DIRECT_EXCHANGE_MULTINODE_REPLY), key = "#{@currentNodeUuidKey}"))
    public void receiveNodeIpcReplyMessage(ReplyMessage ipcReply, Message message, Channel channel) {
        try {
            // 获取指定请求的响应信息
            MultiNodeReplyMessageFuture messageFuture = futureMap.get(ipcReply.getRequestId());
            if (messageFuture != null) {
                // 调用接收方法
                messageFuture.receive(ipcReply);
            }
            // 手动ACK
            this.basicAck(message, channel);
        } catch (Throwable e) {
            log.error("handle receiveNodeIpcReply message Fail ", e);
            this.basicNack(message, channel);
        }

    }

    /**
     * @description: 接收多节点请求
     * @author: HuangBing
     * @date: 2025/8/12 14:44
     * @param oneMessage
     * @param message
     * @param channel
     * @return: void
     **/
    @RabbitListener(concurrency = "3", bindings = @QueueBinding(value = @Queue(durable = "false"),
        exchange = @Exchange(value = FANOUT_EXCHANGE_MULTINODE_REQ, type = ExchangeTypes.FANOUT)))
    public void receiveMultiNodeReqMessage(ReqMessage oneMessage, Message message, Channel channel) {
        ReplyMessage replyMessage = this.commonProcess(oneMessage, message, channel);
        if (replyMessage != null) {
            // 设置全链路请求ID
            replyMessage.setRequestId(oneMessage.getRequestId());
            // 设置当前服务ID
            replyMessage.setServiceId(currentNodeUuidKey);
            // 回复响应给请求者
            messageBus.send(DIRECT_EXCHANGE_MULTINODE_REPLY, oneMessage.getServiceId(), replyMessage);
            return;
        }
        log.error("receiveMultiNodeReqMessage Fail replyMessage is null, ServiceID={},reqMsg:{}", currentNodeUuidKey,
            oneMessage);
    }

    public static void putFuture(MultiNodeReplyMessageFuture future) {
        futureMap.put(future.getRequestId(), future);
    }

    public static void removeFuture(String requestId) {
        futureMap.remove(requestId);
    }
}
