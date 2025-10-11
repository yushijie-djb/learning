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

package com.yushijie.common.core.messagebus.exception;

import cn.keptdata.one2data.core.base.TimeoutException;
import cn.keptdata.one2data.core.common.CommonI18NCode;
import cn.keptdata.one2data.core.i18n.info.FailInfo;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 接收响应消息超时异常
 * @date 2021/8/28 15:47
 */
public class ReceiveMsgTimeoutException extends TimeoutException {
    private static final long serialVersionUID = 275284146196479655L;

    public ReceiveMsgTimeoutException() {
        this(new FailInfo(CommonI18NCode.FAIL.RECEIVE_MSG_TIMEOUT));
    }

    public ReceiveMsgTimeoutException(FailInfo failInfo) {
        super(failInfo);
    }
}
