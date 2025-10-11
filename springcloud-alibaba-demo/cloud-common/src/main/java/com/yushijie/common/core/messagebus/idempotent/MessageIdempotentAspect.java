package com.yushijie.common.core.messagebus.idempotent;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.keptdata.cache.redis.service.RedisService;
import cn.keptdata.one2data.core.api.aspect.utils.AspectUtils;
import cn.keptdata.one2data.core.api.response.ResponseUtil;
import cn.keptdata.one2data.core.common.CommonI18NCode;
import cn.keptdata.one2data.core.i18n.info.FailInfo;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.properties.ApplicationProperties;
import cn.keptdata.one2data.util.common.JSONUtil;
import cn.keptdata.one2data.util.encrypt.EncryptUtil;
import cn.keptdata.one2data.util.net.IPUtils;
import cn.keptdata.one2data.util.uuid.UUIDUtil;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息幂等性处理
 * @date 2023-09-25 16:54
 */
public class MessageIdempotentAspect {

    private static final Log log = LogFactory.getLog();
    /**
     * API 请求之间最小提交间隔(单位：秒)
     */
    private static Long MIN_SUBMIT_INTERVAL_SECONDS;
    /**
     * 是否开启防重复提交 (若间隔时间 > 0 则表示开启,否则表示关闭)
     */
    private static Boolean PREVENT_REPEAT_SUBMIT_SWITCH;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ApplicationProperties applicationProperties;

    @PostConstruct
    public void init() {
        MIN_SUBMIT_INTERVAL_SECONDS = applicationProperties.getPreventRepeatSubmit().getMinSubmitIntervalSeconds();
        PREVENT_REPEAT_SUBMIT_SWITCH = MIN_SUBMIT_INTERVAL_SECONDS > 0;
    }

    /**
     * AOP ->切入点
     */
    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void pointcut() {}

    /**
     * @param joinPoint
     * @return java.lang.Object
     * @author Bob.Yang
     * @description 前置通知，防止请求参数重复提交
     * @date 2023-09-25 16:54
     */
    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        // 消息幂等性注解
        MessageIdempotent idempotentAnnotation = AspectUtils.findAnnotation(joinPoint, MessageIdempotent.class);

        if (Objects.nonNull(idempotentAnnotation)) {
            boolean ignore = idempotentAnnotation.ignore();
        }

        // API 请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // API 请求特征值
        String apiRequestSignature = generateApiRequestSignature(joinPoint, request);
        // API 请求过于频繁,直接反馈
        if (isRepeatSubmit(apiRequestSignature)) {
            return ResponseUtil.fail(new FailInfo(CommonI18NCode.FAIL.API_REQUEST_TOO_FREQUENT));
        }

        try {
            redisService.set(apiRequestSignature, apiRequestSignature, MIN_SUBMIT_INTERVAL_SECONDS);

            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            // API 接口异常抛出,交由统一异常处理器处理
            throw throwable;
        } finally {
            // redisService.del(apiRequestSignature);
        }

    }

    /**
     * @param request http 请求信息
     * @return java.lang.String
     * @author Bob.Yang
     * @description 生成API 请求签名信息 （针对请求资源信息）
     * @date 2022-01-18 17:34
     */
    private String generateApiRequestSignature(JoinPoint joinPoint, HttpServletRequest request) {

        /**
         * 根据以下内容生成API 请求签名信息 0.用户主机地址 1.API URL 2.API 接口名称 3.API 请求参数
         */

        // API 请求地址
        String remoteHost = IPUtils.getRequestIp(request);
        // API URL
        String requestURI = request.getRequestURL().toString();
        // API 访问的实际方法名称
        String method = request.getMethod();
        // 请求参数特征值
        String parameterSignature = UUIDUtil.genUUID();
        Object parameter = AspectUtils.getParameter(joinPoint);
        if (Objects.nonNull(parameter)) {
            parameterSignature = JSONUtil.toJsonStr(parameter);
        }

        StringBuilder apiRequestSignature =
            new StringBuilder().append(remoteHost).append(requestURI).append(method).append(parameterSignature);

        return EncryptUtil.md5Encrypt(apiRequestSignature.toString());
    }

    /**
     * @param apiRequestSignature
     * @return boolean
     * @author Bob.Yang
     * @description 是否重复提交
     * @date 2022-01-18 17:59
     */
    public boolean isRepeatSubmit(String apiRequestSignature) {

        Object apiRequestSignatureObj = redisService.get(apiRequestSignature);

        // API 请求过于频繁,直接反馈
        return Objects.nonNull(apiRequestSignatureObj);
    }
}
