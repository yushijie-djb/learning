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

package com.yushijie.common.enumeration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 基础枚举类接口
 * @date 2021/1/9 15:02
 */
public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> extends Serializable {

    /**
     * @param enumClass
     * @param enumCode
     * @return
     * @author Bob.Yang
     * @description 根据枚举类型和枚举Code 获取相应的枚举实例
     * @date 2021/1/29 11:23
     **/
    static <T extends Enum<T> & BaseEnum<T>> T parseByCode(Class<T> enumClass, String enumCode) {
        for (T enumInstance : enumClass.getEnumConstants()) {
            if (enumInstance.getCode().equals(enumCode)) {
                return enumInstance;
            }
        }

        return null;
    }

    /**
     * @param enumClass
     * @return java.util.List<java.lang.String>
     * @author Bob.Yang
     * @description 获取指定枚举可用的code
     * @date 2021/1/27 16:22
     */
    static <T extends Enum<T> & BaseEnum<T>> List<String> getAvailCodes(Class<T> enumClass) {

        List<String> codes = new ArrayList<>(8);

        for (T enumInstance : enumClass.getEnumConstants()) {
            codes.add(enumInstance.getCode());
        }

        return codes;
    }

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @date 2021/1/27 16:22
     * @description 获取枚举编码
     **/
    default String getCode() {
        return toString().toLowerCase();
    }

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取枚举描述
     * @date 2021/1/27 16:22
     **/
    default String getDesc() {
        return toString();
    }

    default String getName() {
        return null;
    }

    default Class<T> getClazz() {
        return null;
    }
    default  boolean isConfigEnum(){
        return false;
    }
    /**
     * @param value
     * @return boolean
     * @author Bob.Yang
     * @description 是否相等
     * @date 2021/1/27 16:23
     **/
    default boolean equals(String value) {
        return this.getCode().equalsIgnoreCase(value);
    }
}