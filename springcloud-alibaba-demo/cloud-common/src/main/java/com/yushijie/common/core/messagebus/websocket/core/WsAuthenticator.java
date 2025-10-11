package com.yushijie.common.core.messagebus.websocket.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.keptdata.one2data.api.identity.AuthApi;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WEBSocket 认证器
 * @date 2023-03-06 15:04
 */
@Component
public class WsAuthenticator {

    @Autowired
    private AuthApi authApi;

    /**
     * @param accessToken ws 请求Token
     * @return boolean
     * @author Bob.Yang
     * @description WebSocket 认证
     * @date 2023-03-06 15:06
     */
    public WsPrincipal authenticate(String accessToken) throws WsAuthException {

        // TODO: 完善WEBSocket 认证流程，当前默认全部认证通过

        /* //非空认证
        if (StringUtils.isEmpty(accessToken)) {
            throw new WsAuthException("Websocket AccessToken is Empty");
        }
        
        //合法性认证
        boolean verifyResult = authApi.verifyAccessToken(accessToken);
        if (!verifyResult) {
            throw new WsAuthException("Websocket AccessToken Invalid");
        }
        
        //获取关联的用户ID
        String userUuid = authApi.getUserUuidFromAccessToken(accessToken);
        String tenantUuid = "";*/

        // 认证成功,返回完整的认证信息
        return new WsPrincipal(accessToken, "tenantUuid", "userUuid");
    }

}
