package com.yushijie.common.core.messagebus.websocket.receiver.content;

import java.io.Serializable;
import java.util.List;

import cn.keptdata.one2data.header.base.common.ReferenceEnum;
import cn.keptdata.one2data.header.common.enumeration.ResourceType;
import cn.keptdata.one2data.header.common.enumeration.ServiceType;
import cn.keptdata.one2data.header.common.enumeration.SubscribeBusinessName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 存储长任务进度信息订阅
 * @author HuangBing
 * @date 2024/5/8 下午2:06
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressSubscribe implements Serializable {
    /**
     * 需要订阅进度的资源类型
     */
    @ReferenceEnum(ResourceType.class)
    private String resourceType;
    /**
     * 服务类型 预留字段
     */
    @ReferenceEnum(ServiceType.class)
    private String serviceType = ServiceType.STORAGE.getCode();

    /**
     * 业务名称，用于同一服务下业务区分
     */
    @ReferenceEnum(SubscribeBusinessName.class)
    private String businessName = "";

    /**
     * 订阅状态 y：订阅 n:取消订阅
     */
    private String state;

    /**
     * 需要进行操作的资源uuid集合
     */
    List<String> uuids;

    /**
     * 会话id-外部使用请忽略
     */
    private String sessionId;
}
