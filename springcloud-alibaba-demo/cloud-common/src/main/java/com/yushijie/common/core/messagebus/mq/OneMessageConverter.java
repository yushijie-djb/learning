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

package com.yushijie.common.core.messagebus.mq;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import com.google.protobuf.InvalidProtocolBufferException;

import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.core.MessageBusHelper;
import cn.keptdata.one2data.header.base.enumeration.BaseEnum;
import cn.keptdata.one2data.header.message.base.MessageType;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.helper.ProtoBufHelper;
import cn.keptdata.one2data.util.common.JSONUtil;
import org.springframework.stereotype.Component;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description One2Data 系统自定义消息转换器
 * @date 2021/3/12 12:52
 */
@Component
public class OneMessageConverter extends AbstractMessageConverter {

    /**
     * 自定义消息头属性-消息类型
     */
    private static final String CUSTOMIZE_MSG_HEADER_PROPERTY_MSG_TYPE = "message_type";
    /**
     * 默认编码格式
     */
    private static final String DEFAULT_CHARSET = "UTF-8";
    private final Log log = LogFactory.getLog();
    private final String CLASS_NAME = "class_name";
    private final String IS_ARRAY = "is_array";
    /**
     * Spring 内置的默认消息转换器；支持text类型 和 byte[] 类型消息转换
     */
    private final SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();

    /**
     * @param object
     * @param messageProperties
     * @return org.springframework.amqp.core.Message
     * @author Bob.Yang
     * @description 消息转换器，用于将Object 类型转换为 Byte[]数组并以Message类型返回
     * @date 2021/3/12 10:28
     */
    @Override
    protected Message createMessage(Object object, MessageProperties messageProperties) {

        /**
         * 约定目前系统中仅存在四种原始类型的数据：参考：MessageType.class 1. 普通字符串类型 String -> byte[] 2. 普通Java Object 类型 -> 转换为Json->byte[]
         * 3. ProtoBuf 类型的对象 -> 转换 byte[] 4. byte[] 数组类型
         */

        /**
         * 字符串和byte 数组通过 simpleMessageConverter 进行处理
         */

        dynamicSetMsgMetaData(object, messageProperties);

        if (object instanceof byte[]) {
            setMessageTypeInHeader(messageProperties, MessageType.BYTE_ARRAY);
            return simpleMessageConverter.toMessage(object, messageProperties);
        }
        if (object instanceof String) {
            setMessageTypeInHeader(messageProperties, MessageType.SIMPLE_STR);
            return simpleMessageConverter.toMessage(object, messageProperties);
        }

        /**
         * ProtoBuf 和其他类型的Object 对象通过JSON 序列化
         */

        byte[] bytes = null;

        // ProtoBuf 类型消息转换
        if (object instanceof com.google.protobuf.Message) {
            // 设置自定义消息格式：Protobuf
            setMessageTypeInHeader(messageProperties, MessageType.PROTOBUF);

            com.google.protobuf.Message protoBufObj = (com.google.protobuf.Message)object;
            if (log.isDebugEnabled()
                && !MessageBusHelper.isIgnoreForDebug(messageProperties, (Common.Message)protoBufObj)) {
                log.debug("WEB|MQ,Send Message:{}", ProtoBufHelper.format(protoBufObj));
            }

            bytes = protoBufObj.toByteArray();
        } else {
            // 设置自定义消息格式：普通Java 对象
            setMessageTypeInHeader(messageProperties, MessageType.ORDINARY_OBJECT);

            // 其他普通Java 对象通过JSON 序列化
            String jsonData = JSONUtil.toJsonStr(object);
            try {
                bytes = jsonData.getBytes(DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new MessageConversionException("Can not Convert Message,reason:" + e.getMessage());
            }
        }

        // 设置数据和大小
        if (bytes != null) {
            messageProperties.setContentLength(bytes.length);
            return new Message(bytes, messageProperties);
        }

        throw new MessageConversionException("Can not Convert Message!");
    }

    /**
     * @param object
     * @param messageProperties
     * @return void
     * @author Bob.Yang
     * @description 动态设置消息元数据信息
     * @date 2021/7/24 19:39
     */
    private void dynamicSetMsgMetaData(Object object, MessageProperties messageProperties) {
        if (object instanceof Collection) {
            String className;
            Collection coll = (Collection)object;
            if (coll.size() > 0) {
                Object next = coll.iterator().next();
                className = next.getClass().getName();
            } else {
                className = Object.class.getName();
            }
            messageProperties.setHeader(CLASS_NAME, className);
            messageProperties.setHeader(IS_ARRAY, true);
        } else {
            messageProperties.setHeader(CLASS_NAME, object.getClass().getName());
            messageProperties.setHeader(IS_ARRAY, false);
        }
    }

    /**
     * @param message
     * @return java.lang.Object
     * @author Bob.Yang
     * @description 从MQ 中接收消息，将字节类型消息转换为自定义的类型
     * @date 2021/3/12 18:12
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {

        // 获取消息属性
        MessageProperties properties = message.getMessageProperties();

        // 获取消息体
        byte[] body = message.getBody();
        if (properties != null) {

            // 获取消息类型
            MessageType messageType = getMessageTypeInHeader(message);

            // 解析字节数组类型消息
            if (MessageType.BYTE_ARRAY.equals(messageType)) {
                return body;
            }

            // 解析字符串类型消息
            if (MessageType.SIMPLE_STR.equals(messageType)) {
                try {
                    return new String(body, DEFAULT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    log.error("failed to convert from byte[] to String Content");
                    throw new MessageConversionException("failed to convert to Message content", e);
                }
            }

            // 解析普通Object 类型的消息
            if (MessageType.ORDINARY_OBJECT.equals(messageType)) {
                try {
                    String json = new String(body, DEFAULT_CHARSET);
                    return parseOrdinaryObject(properties, json);
                } catch (UnsupportedEncodingException e) {
                    log.error("failed to convert from byte[] to Object Content");
                    throw new MessageConversionException("failed to convert to Message content", e);
                }
            }

            // 解析ProtoBuf 类型消息
            if (MessageType.PROTOBUF.equals(messageType)) {
                return parseFromProtoBufMessage(properties, body);
            }

        }

        // 若为标记类型,默认为ProtoBuf 类型消息进行解析
        return parseFromProtoBufMessage(properties, body);
    }

    /**
     * @param messageProperties
     * @param jsonData
     * @return java.lang.Object
     * @author Bob.Yang
     * @description 解析普通类型的Java 对象
     * @date 2021/7/24 19:47
     */
    private Object parseOrdinaryObject(MessageProperties messageProperties, String jsonData) {
        String className = (String)messageProperties.getHeaders().get(CLASS_NAME);

        try {
            Boolean aBoolean = (Boolean)messageProperties.getHeaders().get(IS_ARRAY);
            if (aBoolean != null && aBoolean) {
                return JSONUtil.parseList(jsonData, Class.forName(className));
            } else {
                return JSONUtil.toBean(jsonData, Class.forName(className));
            }
        } catch (ClassNotFoundException e) {
            log.error("failed to convert from byte[] to Object Content");
            throw new MessageConversionException("failed to convert to Message content", e);
        }

    }

    /**
     * @param properties
     * @param body
     * @return java.lang.Object
     * @author Bob.Yang
     * @description 将二进制字节转换为ProtoBuf 类型的消息
     * @date 2021/4/10 12:16
     */
    private Object parseFromProtoBufMessage(MessageProperties properties, byte[] body) {

        try {
            Common.Message protoMessage = Common.Message.parseFrom(body);

            if (log.isDebugEnabled() && !MessageBusHelper.isIgnoreForDebug(properties, protoMessage)) {
                log.debug("WEB|MQ,Receive Message:{}", ProtoBufHelper.format(protoMessage));
            }

            return protoMessage;
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to convert from byte[] to ProtoBuf Content");
            throw new MessageConversionException("failed to convert to Message content", e);
        }

    }

    /**
     * @param messageProperties
     * @param messageType
     * @return void
     * @author Bob.Yang
     * @description 在消息头中放置自定义的属性，用来标记消息的类型
     * @date 2021/3/12 18:22
     */
    private void setMessageTypeInHeader(MessageProperties messageProperties, MessageType messageType) {
        if (messageProperties == null) {
            messageProperties = new MessageProperties();
        }

        messageProperties.setHeader(CUSTOMIZE_MSG_HEADER_PROPERTY_MSG_TYPE, messageType.getCode());
    }

    /**
     * @return cn.keptdata.one2data.header.message.base.MessageType
     * @author Bob.Yang
     * @description 从消息头中获取自定义的消息属性
     * @date 2021/3/12 18:48
     */
    private MessageType getMessageTypeInHeader(Message message) {

        MessageProperties messageProperties = message.getMessageProperties();

        // 未设置消息属性,默认为ProtoBuf 类型数据
        if (messageProperties == null) {
            return MessageType.PROTOBUF;
        }

        String msgTypeCode = messageProperties.getHeader(CUSTOMIZE_MSG_HEADER_PROPERTY_MSG_TYPE);
        return BaseEnum.parseByCode(MessageType.class, msgTypeCode);
    }

}
