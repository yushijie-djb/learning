package com.yushijie.common.core.messagebus.websocket.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.util.collection.CollectionUtils;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket 辅助工具类
 * @date 2022-04-05 17:41
 */
@Component
public class WsHelper {

    @Autowired
    private WsSessionManager wsSessionManager;

    /**
     * @return boolean
     * @author Bob.Yang
     * @description 判断WebSocket 连接是否是本机连接
     * @date 2023-03-03 11:16
     */
    public boolean isLocalConnection() {

        return true;
    }

    /**
     * @return boolean
     * @author Bob.Yang
     * @description 是否存在全局的websocket 连接
     * @date 2022-04-05 17:45
     */
    public boolean isExistGlobalConnection() {
        return wsSessionManager.getGlobalSessionCount() > 0;
    }

    /**
     * @return boolean
     * @author Bob.Yang
     * @description 是否存在本机的websocket 连接
     * @date 2023-03-07 10:41
     */
    public boolean isExistLocalConnection() {
        return wsSessionManager.getLocalSessionCount() > 0;
    }

    /**
     * @param wsTopic
     * @param receiverUuids 接收者uuid，为空表示广播发送
     * @return boolean
     * @author Bob.Yang
     * @description 当前系统是否存在用户订阅指定主题的消息
     * @date 2022-04-05 19:32
     */
    public boolean hasSubscribe(String wsTopic, List<String> receiverUuids) {
        if (CollectionUtils.isEmpty(receiverUuids)) {
            return wsSessionManager.topicIsSubscribed(WsConst.WS_TOPIC_PATH + "/"+wsTopic);
        }
        List<Boolean> hasUserSubscribeList = CollectionUtils.newArrayList();
        for (String receiverUuid : receiverUuids) {
            boolean hasUserSubscribe = wsSessionManager.topicIsSubscribed(WsConst.WS_P2P_PATH + "/" + receiverUuid + WsConst.WS_TOPIC_PATH + "/" + wsTopic);
            hasUserSubscribeList.add(hasUserSubscribe);
        }
        // 传入的指定用户中只要有至少一个用户订阅了topic，则返回true
        return hasUserSubscribeList.stream().anyMatch(Boolean::booleanValue);
    }

}
