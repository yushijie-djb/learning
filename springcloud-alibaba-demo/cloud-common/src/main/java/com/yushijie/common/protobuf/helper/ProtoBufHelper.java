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

package com.yushijie.common.protobuf.helper;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import cn.keptdata.logging.Log;
import cn.keptdata.logging.LogFactory;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.common.Node;
import cn.keptdata.one2data.util.string.StringUtils;
import cn.keptdata.one2data.util.uuid.UUIDUtil;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 内部ProtoBuf消息辅助类
 * @date 2021/3/17 15:19
 */
public class ProtoBufHelper {

    private static final Log log = LogFactory.getLog();

    /**
     * @param header
     * @param body
     * @return void
     * @author Bob.Yang
     * @description 打包自定义消息
     * @date 2021/3/17 16:17
     */
    public static <T extends Message> Common.Message pack(Common.Header header, T body) {
        String bodyClassSimpleName = body.getDescriptorForType().getFullName();

        String correlationId = header.getCorrelationId();
        // 若Header 关联ID为空,则产生随机请求ID
        if (StringUtils.isEmpty(correlationId)) {
            correlationId = UUIDUtil.genUUID();
        }

        Common.Header headerWithDescriptor =
            header.toBuilder().setBodyTypeDescriptor(bodyClassSimpleName).setCorrelationId(correlationId)
                .setNodeType(Node.NodeType.NT_WEB).setNodeUuidKey("keptdata-one2data-web").build();

        return Common.Message.newBuilder().setBody(Any.pack(body)).setHeader(headerWithDescriptor).build();
    }

    /**
     * @param body
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.Message
     * @author Bob.Yang
     * @description 打包消息
     * @date 2021-11-17 14:07
     */
    public static <T extends Message> Common.Message pack(T body) {
        // 构造消息头
        Common.Header header = Common.Header.newBuilder().setCorrelationId(UUIDUtil.genUUID()).build();

        return pack(header, body);
    }

    /**
     * @param oneMessage
     * @param clazz
     * @return T
     * @author Bob.Yang
     * @description 获取通用消息体内的Body 信息
     * @date 2021/8/18 15:10
     */
    public static <T extends Message> T getBody(Common.Message oneMessage, Class<T> clazz) {
        try {
            return oneMessage.getBody().unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            log.error("WEB | 解析OneMessage 消息体出现异常", e);
            throw new RuntimeException("Get OneMessage Body Exception", e);
        }
    }

    /**
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.BaseAck
     * @author Bob.Yang
     * @description 构造成功响应消息
     * @date 2021/4/16 20:30
     */
    public static Common.BaseAck buildSuccessBaseAck() {
        return Common.BaseAck.newBuilder().setIsSuccess(true).build();
    }

    /**
     * @param errorCode
     * @param errorInfo
     * @return cn.keptdata.one2data.header.message.protobuf.common.Common.BaseAck
     * @author Bob.Yang
     * @description 构造错误的基础响应消息
     * @date 2021-09-29 20:15
     */
    public static Common.BaseAck buildFailBaseAck(Integer errorCode, String errorInfo) {
        return Common.BaseAck.newBuilder().setIsSuccess(false).setErrorCode(errorCode).setErrorInfo(errorInfo).build();
    }

    /**
     * @param oneMessage
     * @return boolean
     * @author Bob.Yang
     * @description 指定消息是否为超时响应消息
     * @date 2021/8/19 20:50
     */
    public static boolean isTimeoutReply(Common.Message oneMessage) {
        // @Reference:CommonMessageFuture.java Line:132
        return oneMessage == null;
    }

    /**
     * @param message
     * @return java.lang.String
     * @author Bob.Yang
     * @description 将ProtoBuf 消息转换为JSON 字符串
     * @date 2021-12-25 17:12
     */
    public static <T extends Message> String format(T message) {
        return ProtoFormatter.toJsonFormat(message);
    }
}
