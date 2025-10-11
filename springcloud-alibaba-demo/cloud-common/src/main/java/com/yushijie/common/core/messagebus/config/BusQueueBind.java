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

package com.yushijie.common.core.messagebus.config;

import cn.keptdata.one2data.core.messagebus.core.MessageBusConst;
import cn.keptdata.one2data.util.string.StringUtils;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 系统消息总线队列绑定配置信息
 * @date 2021/4/17 11:36
 */
public enum BusQueueBind {

    /**
     * WEB 与客户端
     */
    web2Client("one2data.backup.web2client", "one2data.backup.web2client.{device_uuidKey}.#"),
    /**
     * WEB 与日志收集
     */
    web2ClientLogUpload("one2data.ops.client.web2logUpload", "one2data.ops.client.web2logUpload.{device_uuidKey}.#"),
    web2PELogUpload("one2data.ops.pe.web2logUpload", "one2data.ops.pe.web2logUpload.{pe_uuidKey}.#"),
    web2ComputeLogUpload("one2data.ops.compute.web2logUpload", "one2data.ops.compute.web2logUpload.{ecs_uuidKey}.#"),
    web2StorageLogUpload("one2data.ops.storage.web2logUpload", "one2data.ops.storage.web2logUpload.{storage_uuidKey}.#"),
    /**
     * WEB 与客户端升级
     */
    web2ClientUpgrade("one2data.ops.client.web2upgrade", "one2data.ops.client.web2upgrade.{device_uuidKey}.#"),

    /**
     * WEB 与计算节点升级
     */
    web2ComputeUpgrade("one2data.ops.compute.web2upgrade", "one2data.ops.compute.web2upgrade.{device_uuidKey}.#"),

    /**
     * WEB 与存储节点升级
     */
    web2StorageUpgrade("one2data.ops.storage.web2upgrade", "one2data.ops.storage.web2upgrade.{device_uuidKey}.#"),

    /**
     * WEB 与PE节点升级
     */
    web2PeUpgrade("one2data.ops.pe.web2upgrade", "one2data.ops.pe.web2upgrade.{device_uuidKey}.#"),

    /**
     * WEB 与PE节点升级
     */
    web2InitiatorUpgrade("one2data.ops.initiator.web2upgrade", "one2data.ops.initiator.web2upgrade.{device_uuidKey}.#"),

    /**
     * WEB 与 PE
     */
    web2PE("one2data.recovery.web2pe", "one2data.recovery.web2pe.{pe_uuidKey}.#"),
    /**
     * WEB 与 计算节点
     */
    web2Compute("one2data.compute.web2compute", "one2data.compute.web2compute.{ecs_uuidKey}.#"),

    /**
     * WEB 与 存储服务
     */
    web2Storage("one2data.storage.web2storage", "one2data.storage.web2storage.{storage_uuidKey}.#"),


    web2Backend("one2data.storage.web2backend", "one2data.storage.web2backend.{storage_uuidKey}.#"),

    /**
     * WEB 与 挂载Initiator 服务
     */
    web2Initiator("one2data.mount.web2initiator", "one2data.mount.web2initiator.{initiator_uuidKey}.#"),

    /**
     * WEB 与存储远程同步服务之间
     */
    web2Remote("one2data.storage.web2remote", "one2data.storage.web2remote.{storage_uuidKey}.#"),
    /**
     * WEB 与验证客户端
     */
    web2vmagent("one2data.instance.web2vmagent", "one2data.instance.web2vmagent.{vmagent_uuidKey}.#"),
    /**
     * WEB 与Auth 服务
     */
    web2Auth("one2data.auth.web2auth", "one2data.auth.web2auth"),

    /**
     * WEB 进程内通讯 （RPC）
     */
    webIPC(MessageBusConst.WEB_IPC_EXCHANGE, "one2data.web.ipc.{serviceIdentifier}");

    private final String exchangeName;

    private final String bindingKeyPattern;

    BusQueueBind(String exchangeName, String bindingKeyPattern) {
        this.exchangeName = exchangeName;
        this.bindingKeyPattern = bindingKeyPattern;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getExchangeName(String placeholder) {
        return StringUtils.formatBraces(this.exchangeName, placeholder);
    }

    public String getBindingKey() {
        return this.bindingKeyPattern;
    }

    public String getBindingKey(String placeholder) {
        return StringUtils.formatBraces(this.bindingKeyPattern, placeholder);
    }
}
