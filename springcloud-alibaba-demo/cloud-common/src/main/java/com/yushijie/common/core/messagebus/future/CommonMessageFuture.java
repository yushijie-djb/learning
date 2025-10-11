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

package com.yushijie.common.core.messagebus.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.keptdata.one2data.core.common.CommonI18NCode;
import cn.keptdata.one2data.core.exception.CommonException;
import cn.keptdata.one2data.core.i18n.info.FailInfo;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.core.AsyncMessageCoordinator;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.helper.ProtoBufHelper;
import lombok.Getter;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 通用消息（One2Data 通用ProtoBuf消息）接收
 * @date 2021/7/1 17:16
 */
public class CommonMessageFuture extends MessageFuture<Common.Message> {

    private static final Log log = LogFactory.getLog();
    /**
     * 等待响应消息的计数器
     */
    private final CountDownLatch latch = new CountDownLatch(1);
    /**
     * 存放待接收的响应消息
     */
    private final Common.Message[] replyMessages = new Common.Message[1];
    /**
     * 当前请求消息
     */
    private final Common.Message request;
    /**
     * 消息超时时间（单位：毫秒）
     */
    private final long timeoutMillis;
    /**
     * 当前请求消息ID
     */
    @Getter
    private final String requestId;

    public CommonMessageFuture(Common.Message request, long timeoutMillis) {
        this.request = request;
        this.timeoutMillis = timeoutMillis;
        this.requestId = request.getHeader().getCorrelationId();
        AsyncMessageCoordinator.putFuture(this);
    }

    @Override
    public Common.Message getReply(long milliseconds) {
        try {
            boolean success = latch.await(milliseconds, TimeUnit.MILLISECONDS);
            if (!success || replyMessages[0] == null) {
                timeout();
                log.error("WEB|MQ 发送RPC请求消息(RequestID:{})接收超时,消息Body:{}", requestId,
                    ProtoBufHelper.format(request.getBody()));
                return createTimeoutReply();
            }
            return replyMessages[0];
        } catch (InterruptedException e) {
            log.error("Wait Reply Message Exception", e);
            throw new CommonException(new FailInfo(CommonI18NCode.FAIL.UN_KNOWN_SYS_ERROR, e.getMessage()));
        }
    }

    @Override
    public void receive(Common.Message reply) {
        AsyncMessageCoordinator.removeFuture(reply.getHeader().getCorrelationId());
        log.debug("Receive Reply Message, RequestID: {}", reply.getHeader().getCorrelationId());

        if (latch.getCount() < 1) {
            log.warn("Receive Reply Message but already receive, RequestID: {}", reply.getHeader().getCorrelationId());
            return;
        }

        replyMessages[0] = reply;
        latch.countDown();

        log.debug("Receive Reply Message, and notified waiting thread, RequestID: {}",
            reply.getHeader().getCorrelationId());
    }

    @Override
    public void timeout() {
        AsyncMessageCoordinator.removeFuture(requestId);
    }

    @Override
    public Common.Message getReply() {
        return getReply(timeoutMillis);
    }

    @Override
    public Common.Message createTimeoutReply() {
        // 若接收同步消息超时，则超时的响应结果为空
        return null;
    }

    @Override
    public String toString() {
        return "CommonMessageFuture{" + "requestId='" + requestId + '\'' + " completed=" + (latch.getCount() < 1) + '}';
    }

}
