package com.yushijie.common.core.messagebus.future;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.hutool.extra.spring.SpringUtil;
import cn.keptdata.one2data.api.operation.ApplicationApi;
import cn.keptdata.one2data.core.common.CommonI18NCode;
import cn.keptdata.one2data.core.exception.CommonException;
import cn.keptdata.one2data.core.i18n.info.FailInfo;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.core.MultiNodeIPCMessageCoordinator;
import cn.keptdata.one2data.core.messagebus.mq.base.BaseMessageReceiver;
import cn.keptdata.one2data.header.message.base.NeedReplyMessage;
import cn.keptdata.one2data.header.message.base.OneMessage;
import cn.keptdata.one2data.header.message.base.ReplyMessage;
import cn.keptdata.one2data.util.collection.CollectionUtils;
import lombok.Getter;

/**
 * @description: 多节点IPC消息异步处理类，用于处理需要回复的消息。 * 该类会等待所有管理节点的响应， * 如果超时未收到某个节点的响应，将记录错误日志。
 * @author: HuangBing
 * @date: 2025/8/12 14:13
 **/
public class MultiNodeReplyMessageFuture extends MultiNodeMessageFuture {

    private static final Log log = LogFactory.getLog();
    /**
     * 等待响应消息的计数器
     */
    private final CountDownLatch latch;
    /**
     * 存放待接收的响应消息
     */
    private final Map<String, ReplyMessage> replyMessages = new HashMap<>();
    /**
     * 当前请求消息
     */
    private final NeedReplyMessage request;

    /**
     * 消息超时时间（单位：毫秒）
     */
    private final long timeoutMillis;
    /**
     * 当前请求消息ID
     */
    @Getter
    private final String requestId;

    /**
     * @description: 构造函数，初始化请求消息、超时时间和响应计数器。对应的消息处理器需要继承同步消息处理器 SyncMessageHandler，并在处理器中发送Ipc响应到指定节点的
     *               one2data.ipcReply.#{@nodeUuidKey}
     * @author: HuangBing
     * @date: 2025/8/12 14:51
     * @param request
     * @param timeoutMillis
     * @return: null
     **/
    public MultiNodeReplyMessageFuture(NeedReplyMessage request, long timeoutMillis) {
        this.request = request;
        this.timeoutMillis = timeoutMillis;
        this.requestId = request.getRequestId();
        ApplicationApi applicationApi = SpringUtil.getBean(ApplicationApi.class);
        if (applicationApi == null) {
            throw new CommonException(
                new FailInfo(CommonI18NCode.FAIL.UN_KNOWN_SYS_ERROR, "ApplicationApi bean not found"));
        }
        // 获取所有在线节点
        List<String> onlineUuidKeys = applicationApi.findOnlineUuidKeys();
        if (CollectionUtils.isEmpty(onlineUuidKeys)) {
            throw new CommonException(
                new FailInfo(CommonI18NCode.FAIL.UN_KNOWN_SYS_ERROR, "No management nodes found"));
        }
        // 设置计数器为节点数量
        this.latch = new CountDownLatch(onlineUuidKeys.size());
        // 默认设置空值，用于超时时判断所在超时节点
        onlineUuidKeys.stream().parallel().forEach(uuidKey -> replyMessages.put(uuidKey, null));
        // 将当前future对象存入futureMap缓存中，用于收到响应时消息能被接收
        MultiNodeIPCMessageCoordinator.putFuture(this);
    }

    /**
     * @description: 接收回复消息，处理响应结果。
     **/
    @Override
    public void receive(ReplyMessage reply) {
        MultiNodeIPCMessageCoordinator.removeFuture(reply.getRequestId());
        log.debug("Receive Reply Message, RequestID: {}", reply.getRequestId());

        if (latch.getCount() < 1) {
            log.warn("Receive Reply Message but all already receive, RequestID: {}", reply.getRequestId());
            return;
        }
        if (reply.getBody() != null) {
            try {
                Class<?> bodyClass = Class.forName(reply.getHeaders().get(OneMessage.BODY_TYPE).toString());
                reply.setBody((Serializable)BaseMessageReceiver.getBody(reply, bodyClass));
            } catch (Throwable e) {
                log.error("Failed to deserialize reply body", e);
            }

        }
        replyMessages.put(reply.getServiceId(), reply);
        latch.countDown();

        log.debug("Receive Reply Message, and notified waiting thread, RequestID: {}", reply.getRequestId());
    }

    @Override
    public Map<String, ReplyMessage> getReply() {
        try {
            boolean success = latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
            if (!success) {
                Iterator<Map.Entry<String, ReplyMessage>> it = replyMessages.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ReplyMessage> entry = it.next();
                    if (entry.getValue() == null) {
                        it.remove();
                        log.error("WEB|MQ 发送RPC请求消息(RequestID:{},Node:{})接收处理超时,消息Body:{}", entry.getKey(), request);
                    }
                }
                if (CollectionUtils.isEmpty(replyMessages)) {
                    log.error("WEB|MQ 发送RPC请求消息(RequestID:{}) 所有节点接收处理超时,消息Body:{}", requestId, request);
                    return replyMessages;
                }
            }
            return replyMessages;
        } catch (InterruptedException e) {
            log.error("Wait Reply Message Exception", e);
            throw new CommonException(new FailInfo(CommonI18NCode.FAIL.UN_KNOWN_SYS_ERROR, e.getMessage()));
        }
    }
}
