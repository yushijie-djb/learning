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

package com.yushijie.common.core.messagebus.websocket.common;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.keptdata.one2data.header.message.base.OneMessage;
import cn.keptdata.one2data.util.date.DateUtil;
import cn.keptdata.util.collection.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 推送消息实体
 * @date 2021/7/21 23:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushMessage<T extends Serializable> extends OneMessage<T> implements Serializable {

    /**
     * 推送消息的主题，详见WsTopic.class
     */
    private String topic;

    /**
     * 接收者唯一ID(为空，表示广播发送)
     */
    @JsonIgnore
    private List<String> receiverUuids;

    /**
     * 消息产生时间戳(毫秒)
     */
    private Long timestamp = DateUtil.getTimestampMills();

    @Override
    public String toString() {
        return "PushMessage{" + "topic='" + topic + '\'' + ", timestamp=" + timestamp + "} " + super.toString();
    }

    /**
     * @return boolean
     * @author Bob.Yang
     * @description 当前消息是否为广播消息
     * @date 2023-03-07 11:25
     */
    public boolean isBroadcastMessage() {
        return ArrayUtil.isEmpty(receiverUuids);
    }

}