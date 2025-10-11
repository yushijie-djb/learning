package com.yushijie.common.core.messagebus.core;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息总线中的常量
 * @date 2021-12-21 11:35
 */
public interface MessageBusConst {

    /**
     * WEB IPC 交换机名称
     */
    String WEB_IPC_EXCHANGE = "one2data.web.ipc";

    String WEB_IPC_COMMON_QUEUE = "one2data.web.ipc.common";
    /**
     * WEB IPC 恢复模块
     */
    String RECOVERY_IPC_EXCHANGE = "one2data.web.ipc.recovery";

    /**
     * WEB IPC 绑定键前缀
     */
    public static final String WEB_IPC_BINDING_KEY_PREFIX = "one2data.web.ipc.";

    /**
     * 消息相关配置
     */
    interface Message {
        /**
         * 是否为异步转同步消息
         */
        String CUSTOM_ASYNC_2SYNC_HEADER = "isAsync2SyncMessage";
    }

}
