package com.yushijie.common.core.messagebus.websocket.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import cn.keptdata.cache.redis.service.RedisService;
import cn.keptdata.one2data.api.operation.ApplicationApi;
import cn.keptdata.one2data.api.system.CommonApi;
import cn.keptdata.one2data.core.common.RedisKeyPrefix;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.websocket.core.WsPrincipal;
import cn.keptdata.one2data.util.collection.CollectionUtils;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WS Session 连接管理
 * @date 2023-03-03 11:50
 */
@Component
public class WsSessionManager {

    @Resource
    private ApplicationApi applicationApi;

    private static final Log log = LogFactory.getLog();
    private static String MACHINE_SERIAL_NUMBER = null;
    /**
     * 本地存储的WebSocket Session 连接
     */
    private static final ConcurrentHashMap<String, WebSocketSession> LOCAL_WS_SESSION_POOLS = new ConcurrentHashMap(16);
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private CommonApi commonApi;
    @PreDestroy
    public void destroy() {
        /**
         * 项目关闭时，移除与本机连接数与订阅信息
         */
        initWsCache();
    }

    /**
     * @param socketSession
     * @return void
     * @author Bob.Yang
     * @description 建立websocket
     * @date 2023-03-06 14:22
     */
    public void add(WebSocketSession socketSession) {
        /**
         * 加入本机Session 连接池 记录全局连接
         */
        try {
            WsPrincipal wsPrincipal = (WsPrincipal)socketSession.getPrincipal();

            // Session 唯一ID
            String sessionId = socketSession.getId();
            // websocket 关联的用户唯一ID
            String userUuid = wsPrincipal.getUserUuid();
            handleWsConnect(sessionId, userUuid);
        } catch (Exception e) {
            log.error("保存WEBSocketSession 异常", e);
        }
    }

    
    /**
     * @param sessionId
     * @param userUuid
     * @return void
     * @description 处理websocket连接
     * @date 2023-03-06 21:23
     */
    public void handleWsConnect(String sessionId, String userUuid) {

        // 保存全局Session 信息
        this.saveGlobalSession(sessionId, userUuid);

        this.bindUserAndSession(userUuid, sessionId);

        this.incrGlobalSessionCount(userUuid, sessionId);
    }

    /**
     * @param sessionId
     * @param userUuid
     * @return void
     * @author Bob.Yang
     * @description 保存Session 信息
     * @date 2023-03-06 21:23
     */
    public void saveGlobalSession(String sessionId, String userUuid) {
        redisService.hset(RedisKeyPrefix.WS_SESSION.getKeyName(sessionId), "sessionId", sessionId);
        redisService.hset(RedisKeyPrefix.WS_SESSION.getKeyName(sessionId), "machineId", MACHINE_SERIAL_NUMBER);
        redisService.hset(RedisKeyPrefix.WS_SESSION.getKeyName(sessionId), "userUuid", userUuid);
    }

    /**
     * @param sessionId
     * @return void
     * @author Bob.Yang
     * @description 删除Session 信息
     * @date 2023-03-06 21:23
     */
    public void deleteGlobalSession(String sessionId) {
        redisService.del(RedisKeyPrefix.WS_SESSION.getKeyName(sessionId));
    }

    /**
     * @param socketSession
     * @return void
     * @author Bob.Yang
     * @description 断开websocket 连接
     * @date 2023-03-06 14:22
     */
    public void remove(WebSocketSession socketSession) {
        /**
         * 从本机Session 连接池内移除 从Redis 中移除
         */
        try {

            String sessionId = socketSession.getId();
            handleWsDisConnect(sessionId);
        } catch (Exception e) {
            log.error("移除WEBSocketSession 异常", e);
        }
    }

    /**
     * @description: 处理websocket断开连接
     * @author: HuangBing
     * @date: 2024/11/12 17:40
     * @param sessionId
     * @return: void
     **/
    public void handleWsDisConnect(String sessionId) {

        // 解绑Session 与用户的绑定关系
        this.unbindSessionAndUser(sessionId);
        // 解除当前session的进度订阅
        this.deleteSubscribeBySession(sessionId);
        // 删除Session 的信息
        this.deleteGlobalSession(sessionId);

        this.descGlobalSessionCount();
    }

    /**
     * @param userUuid
     * @param sessionId
     * @return void
     * @author Bob.Yang
     * @description session 和 用户建立绑定关系
     * @date 2023-03-06 21:18
     */
    public void bindUserAndSession(String userUuid, String sessionId) {
        redisTemplate.opsForList().rightPush(RedisKeyPrefix.WS_USER_SESSION.getKeyName(userUuid), sessionId);
    }

    /**
     * @param sessionId
     * @return void
     * @author Bob.Yang
     * @description 解绑用户和session 之间的关系
     * @date 2023-03-06 21:18
     */
    public void unbindSessionAndUser(String sessionId) {

        String userUuid = redisService.hget(RedisKeyPrefix.WS_SESSION.getKeyName(sessionId), "userUuid");
        if (Objects.nonNull(userUuid)) {
            redisTemplate.opsForList().leftPushIfPresent(RedisKeyPrefix.WS_USER_SESSION.getKeyName(userUuid),
                sessionId);
        }

    }

    /**
     * @return void
     * @author Bob.Yang
     * @description 增加Session 的数量
     * @date 2023-03-07 10:28
     */
    public void incrGlobalSessionCount(String sessionId, String userUuid) {
        long incr = redisService.hIncr(RedisKeyPrefix.NODE_WS_SESSION_COUNT.getPrefix(), MACHINE_SERIAL_NUMBER,1);
        if (incr <=0) {
            //不正确的计数-重置节点对应缓存
            initWsCache();
            //重新处理ws的连接
            this.handleWsConnect(sessionId, userUuid);
        }
    }

    /**
     * @return void
     * @author Bob.Yang
     * @description 减少节点 Session 的数量
     * @date 2023-03-07 10:30
     */
    public void descGlobalSessionCount() {
        long decr = redisService.hDecr(RedisKeyPrefix.NODE_WS_SESSION_COUNT.getPrefix(), MACHINE_SERIAL_NUMBER,1);
        if (decr <= 0) {
            //当前节点的会话已被全部关闭，清空缓存
            initWsCache();
        }
    }

    /**
     * @return long
     * @author Bob.Yang
     * @description 获取全局的Session 的数量
     * @date 2023-03-07 10:33
     */
    public int getGlobalSessionCount() {
        //获取所有节点计数
        Map<String, Integer> allNodeSessionCount = redisService.hmget(RedisKeyPrefix.NODE_WS_SESSION_COUNT.getPrefix());
        if (CollectionUtils.isEmpty(allNodeSessionCount)) {
            return 0;
        }
        //获取总计数
        return allNodeSessionCount.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * @return long
     * @author Bob.Yang
     * @description 获取本地session 的数量
     * @date 2023-03-07 10:38
     */
    public int getLocalSessionCount() {

        return LOCAL_WS_SESSION_POOLS.size();
    }


    /**
     * @description: 删除当前session的进度订阅
     * @author: HuangBing
     * @date: 2024/7/12 下午2:50
     * @param sessionId
     * @return: void
     **/
    private void deleteSubscribeBySession(String sessionId) {
        try {
            final List<String> subscribedUuidsBySession = getSubscribedUuidsBySession(sessionId);
            if (CollectionUtils.isNotEmpty(subscribedUuidsBySession)){
                commonApi.rollBackSubscribeUuids(RedisKeyPrefix.PROGRESS_SUBSCRIBE_COUNTER, subscribedUuidsBySession, true,true);
                redisService.del(RedisKeyPrefix.WS_SUBSCRIBED_UUIDS_SESSION.getKeyName(sessionId));
            }
        }catch (Exception e){
            log.error("Delete the progress subscribe of the current session error", e);
        }
    }

    /**
     * @description: 绑定订阅uuid和session 之间的关系
     * @author: HuangBing
     * @date: 2024/7/12 下午2:35
     * @param subscribedUuids
     * @param sessionId
     * @return: void
     **/
    public void bindSubscribedUuidsAndSession(List<String> subscribedUuids, String sessionId) {
        redisService.lSet(RedisKeyPrefix.WS_SUBSCRIBED_UUIDS_SESSION.getKeyName(sessionId), subscribedUuids,60*60*24);
    }

    /**
     * @description: 获取session订阅的uuids列表
     * @author: HuangBing
     * @date: 2024/7/12 下午2:35
     * @param sessionId
     * @return: void
     **/
    public List<String> getSubscribedUuidsBySession(String sessionId) {
        return redisTemplate.opsForList().range(RedisKeyPrefix.WS_SUBSCRIBED_UUIDS_SESSION.getKeyName(sessionId), 0,-1);
    }

    /**
     * @description: 增加topic订阅计数
     * @author: HuangBing
     * @date: 2024/11/12 16:24
     * @param topic
     * @return: void
     **/
    public void addTopicSubscribeCount(String topic) {
        redisService.hIncr(RedisKeyPrefix.WS_TOPIC_SUBSCRIBE_COUNTER.getKeyName(MACHINE_SERIAL_NUMBER), topic, 1);
    }

    /**
     * @description: 减少订阅计数器
     * @author: HuangBing
     * @date: 2024/11/12 16:24
     * @param topic
     * @return: void
     **/
    public void subtractTopicSubscribeCount(String topic) {
        long count = redisService.hDecr(RedisKeyPrefix.WS_TOPIC_SUBSCRIBE_COUNTER.getKeyName(MACHINE_SERIAL_NUMBER), topic, 1);
        if (count <= 0) {
            //订阅计数器小于等于0，删除该topic
            redisService.hdel(RedisKeyPrefix.WS_TOPIC_SUBSCRIBE_COUNTER.getKeyName(MACHINE_SERIAL_NUMBER), topic);
        }
    }

    /**
     * @description: 判断topic是否被订阅
     * @param topic
     * @return: boolean
     **/
    public boolean topicIsSubscribed(String topic) {
        //获取所有节点的uuidKey
        Set<String> uuidKeys = redisService.sGet(RedisKeyPrefix.ONLINE_MANAGEMENT_NODE_UUID_KEYS.getPrefix());
        List<Boolean> resultList = uuidKeys.parallelStream().map(uuidKey -> {
            //根据节点uuidKey 获取计数，判断是否订阅
            Integer count = redisService.hget(RedisKeyPrefix.WS_TOPIC_SUBSCRIBE_COUNTER.getKeyName(uuidKey), topic);
            return count != null && count > 0;
        }).collect(Collectors.toList());
        //只要有任意一个节点订阅该topic,则认为已订阅
        return resultList.stream().anyMatch(Boolean::booleanValue);
    }

    /**
     * @description: 初始化
     * @author: HuangBing
     * @date: 2024/11/29 22:27
     * @return: void
     **/
    @PostConstruct
    public void init(){
        MACHINE_SERIAL_NUMBER=applicationApi.getManagementNodeUuidKey();
        //初始化ws缓存，防止数据有误(服务启动时，可以认为当前管理节点没有任何ws连接)
        initWsCache();
    }

    /**
     * @description: 初始化ws缓存，防止数据有误
     * @author: HuangBing
     * @date: 2024/11/12 17:33
     * @return: void
     **/
    public void initWsCache() {
        //删除节点的对应缓存
        redisService.hdel(RedisKeyPrefix.NODE_WS_SESSION_COUNT.getPrefix(),MACHINE_SERIAL_NUMBER);
        redisService.del(RedisKeyPrefix.WS_TOPIC_SUBSCRIBE_COUNTER.getKeyName(MACHINE_SERIAL_NUMBER));
    }
}
