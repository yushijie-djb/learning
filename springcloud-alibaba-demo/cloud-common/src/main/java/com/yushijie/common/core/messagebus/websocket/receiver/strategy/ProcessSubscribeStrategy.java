package com.yushijie.common.core.messagebus.websocket.receiver.strategy;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.keptdata.one2data.api.system.CommonApi;
import cn.keptdata.one2data.core.common.RedisKeyPrefix;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.websocket.receiver.content.ProgressSubscribe;
import cn.keptdata.one2data.header.common.enumeration.YONEnum;

/**
 * @description 订阅消息处理策略接口
 * @author HuangBing
 * @date 2024/5/10 上午10:37
 * @version 1.0
 */
@Component
public abstract class ProcessSubscribeStrategy {

    protected static final Log logger = LogFactory.getLog();

    @Resource
    private CommonApi commonApi;

    public abstract String serviceType();

    /**
     * @description: 处理消息
     * @author: HuangBing
     * @date: 2024/5/10 上午10:36
     * @param message 消息
     * @return: void
     **/
    public abstract void processMessage(ProgressSubscribe message);

    /**
     * @description: 处理消息之前,过滤出实际需要发送消息的uuid
     * @author: HuangBing
     * @date: 2024/5/14 上午11:43
     * @param message
     * @return: void
     **/
    public final void beforeProcessMessage(ProgressSubscribe message) {
        // 是否订阅
        boolean subscribe = YONEnum.Y.getCode().equals(message.getState());
        commonApi.saveSubscribeUuidsForSession(message);
        List<String> needSendSubscribeUuids = commonApi
            .getNeedSendSubscribeUuids(RedisKeyPrefix.PROGRESS_SUBSCRIBE_COUNTER, message.getUuids(), subscribe);
        message.setUuids(needSendSubscribeUuids);
    }

    /**
     * @description: 捕获processMessage方法抛出的异常并处理
     * @author: HuangBing
     * @date: 2024/7/12 上午11:11
     * @param message
     * @return: void
     **/
    public final void afterThrowingProcessMessage(ProgressSubscribe message) {
        // 是否订阅
        boolean subscribe = YONEnum.Y.getCode().equals(message.getState());
        commonApi.rollBackSubscribeUuids(RedisKeyPrefix.PROGRESS_SUBSCRIBE_COUNTER, message.getUuids(), subscribe,
            false);
    }
}
