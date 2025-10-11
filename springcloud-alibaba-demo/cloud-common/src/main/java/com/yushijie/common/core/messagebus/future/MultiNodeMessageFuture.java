package com.yushijie.common.core.messagebus.future;

import java.util.Map;

import cn.keptdata.one2data.header.message.base.ReplyMessage;

/**
 * @description: 多节点消息Future
 * @author: HuangBing
 * @date: 2025/8/12 11:53
 **/
public abstract class MultiNodeMessageFuture {

    public abstract void receive(ReplyMessage reply);

    /**
     * @description: 获取当前多节点请求的响应体
     * @author: HuangBing
     * @date: 2025/8/12 14:08
     * @return: java.util.Map<nodeUuidKey, Reply>
     **/
    public abstract Map<String, ReplyMessage> getReply();
}
