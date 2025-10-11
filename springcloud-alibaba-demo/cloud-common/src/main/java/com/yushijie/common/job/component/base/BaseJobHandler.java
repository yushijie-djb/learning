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

package com.yushijie.common.job.component.base;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description JobHandler 接口
 * @date 2021/8/27 21:16
 */
public abstract class BaseJobHandler {

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取任务处理器标识符
     * @date 2021/8/27 21:20
     */
    public String getIdentifier() {
        return JobHelper.getJobHandlerIdentifier(getClass());
    }

}
