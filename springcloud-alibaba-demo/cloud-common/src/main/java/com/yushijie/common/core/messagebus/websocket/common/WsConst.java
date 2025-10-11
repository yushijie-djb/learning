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

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket 模块常量
 * @date 2021/7/24 14:13
 */
public interface WsConst {

    /**
     * WS-MQ广播交换机
     */
    String WS_FANOUT_MQ_EXCHANGE = "one2data.web.websocket";

    /**
     * 客户端推送路径
     */
    String WS_CLIENT_PUSH_PATH = "/ws-push";

    /**
     * Ws 端点
     */
    String WS_ENDPOINT = "/websocket";

    /**
     * 主题消息路径前缀
     */
    String WS_TOPIC_PATH = "/topic";

    /**
     * 点对点消息推送地址前缀
     */
    String WS_P2P_PATH = "/p2p";

    /**
     * WebSocket Token字符串关键字
     */
    String WS_TOKEN_KEY = "token";
    /**
     * WebSocket message topic前缀关键字
     */
    String WS_PLAY_LOAD_TOPIC_PREFIX = "id:";
    /**
     * WebSocket message playload分隔符
     */
    String WS_PLAY_LOAD_REGEX = "\\s+";

    /**
     * WebSocket message topic替换字符串
     */
    String WS_PLAY_LOAD_TOPIC_REPLACEMENT = "";
}
