package com.yushijie.common.core.messagebus.delaymessage;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import cn.keptdata.one2data.header.message.base.OneMessage;

/**
 * @author HuangBing
 * @version 1.0
 * @description 延迟消息
 * @date 2024/12/12 16:14
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DelayMessage<T extends Serializable> extends OneMessage<T> implements Serializable {

        /**
        * 延迟时间 单位(秒) 必填参数
         * 支持最大延迟时间（2147483秒 约 24.85天）
         * <a href="https://github.com/rabbitmq/rabbitmq-delayed-message-exchange">关于最大支持的延迟时间的参考链接</a>
        */
        private Integer delaySeconds;

        /**
         * 必填参数
        * 延迟任务类型 详见 @Enum  DelayTaskEnum
        */
        private String type;

        /**
         * 资源唯一标识
         */
        private String resourceUuid;

        /**
         * 资源类型 详见 @Enum  ResourceType
         */
        private String resourceType;
}
