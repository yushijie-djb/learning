package com.yushijie.common.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 资源抽象接口
 * @date 2021-12-23 13:53
 */
public interface Resource {

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取资源
     * @date 2021/8/23 16:53
     */
    @JsonIgnore
    String getResourceUuid();

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取资源类型
     * @date 2021/8/23 16:54
     */
    @JsonIgnore
    String getResourceType();

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取资源名称
     * @date 2021/8/23 17:07
     */
    @JsonIgnore
    String getResourceName();

    /**
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取自定义的资源名称（默认于资源名称保持一致）
     * @date 2023-02-10 13:59
     */
    @JsonIgnore
    default String getAdvanceResourceName() {
        return this.getResourceName();
    }

    @JsonIgnore
    default String getUSAdvanceResourceName() {
        return this.getResourceName();
    }
}
