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

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息Future
 * @date 2021/7/1 12:47
 */
public abstract class MessageFuture<T> {

    /**
     * @param reply
     * @return void
     * @author Bob.Yang
     * @description 接收响应消息
     * @date 2021/7/1 17:39
     */
    public abstract void receive(T reply);

    /**
     * @return void
     * @author Bob.Yang
     * @description 消息接收超时处理
     * @date 2021/7/1 17:39
     */
    public abstract void timeout();

    /**
     * @return T
     * @author Bob.Yang
     * @description 获取消息响应结果
     * @date 2021/7/1 17:39
     */
    public abstract T getReply();

    /**
     * @param timeoutMillis
     * @return T
     * @author Bob.Yang
     * @description 在指定时间内(毫秒)获取消息响应结果
     * @date 2021/7/1 17:40
     */
    public abstract T getReply(long timeoutMillis);

    /**
     * @return T
     * @author Bob.Yang
     * @description 创建超时响应消息
     * @date 2021/7/1 17:40
     */
    public abstract T createTimeoutReply();
}
