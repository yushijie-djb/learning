package com.yushijie.common.core.messagebus.websocket.core;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description ws 认证异常
 * @date 2023-03-06 15:07
 */
public class WsAuthException extends Exception {

    public WsAuthException() {}

    public WsAuthException(String message) {
        super(message);
    }

    public WsAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public WsAuthException(Throwable cause) {
        super(cause);
    }

    public WsAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
