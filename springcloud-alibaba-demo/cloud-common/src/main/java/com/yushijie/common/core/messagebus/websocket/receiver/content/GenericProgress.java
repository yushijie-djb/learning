package com.yushijie.common.core.messagebus.websocket.receiver.content;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 存储长任务进度信息
 * @author HuangBing
 * @date 2024/5/8 下午2:06
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericProgress implements Serializable {
    /**
     * 资源uuid
     */
    private String uuid;

    /**
     * 进度值
     */
    private Double progress;


    /**
     * 剩余时间
     */
    private Long leftSecond;
}
