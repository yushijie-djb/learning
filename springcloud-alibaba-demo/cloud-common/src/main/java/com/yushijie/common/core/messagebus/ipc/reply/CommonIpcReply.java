package com.yushijie.common.core.messagebus.ipc.reply;

import java.io.Serializable;

import cn.keptdata.one2data.header.message.base.ReplyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 通用IPC 响应结果
 * @date 2021-12-21 12:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CommonIpcReply extends ReplyMessage {

    /**
     * 请求是否处理成功
     */
    private boolean success;
    /**
     * 响应码 0: 表示成功 其他：表示失败
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;

    private CommonIpcReply(Serializable body, boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
        setBody(body);
    }

    public static CommonIpcReply success(Serializable body) {
        return new CommonIpcReply(body, true, 0, null);
    }

    public static CommonIpcReply fail(Integer code, String message) {
        return new CommonIpcReply(null, false, code, message);
    }
}
