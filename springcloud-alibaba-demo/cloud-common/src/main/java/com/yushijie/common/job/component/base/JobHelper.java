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

package com.yushijie.common.job.component.base;

import cn.keptdata.cache.redis.service.RedisService;
import cn.keptdata.one2data.core.base.BaseException;
import cn.keptdata.one2data.core.common.RedisKeyPrefix;
import cn.keptdata.one2data.core.i18n.I18NResource;
import cn.keptdata.one2data.core.i18n.info.FailInfo;
import cn.keptdata.one2data.core.messagebus.MessageBus;
import cn.keptdata.one2data.core.messagebus.websocket.common.WsTopic;
import cn.keptdata.one2data.core.spring.SpringContextUtil;
import cn.keptdata.one2data.header.base.common.Resource;
import cn.keptdata.one2data.header.common.enumeration.ServiceModuleEnum;
import cn.keptdata.one2data.header.job.entity.Job;
import cn.keptdata.one2data.header.job.entity.JobPeriod;
import cn.keptdata.one2data.header.job.push.JobProgressInfo;
import cn.keptdata.one2data.header.job.push.JobPushInfo;
import cn.keptdata.one2data.header.operation.log.enumeration.OperateTypeEnum;
import cn.keptdata.one2data.job.component.base.annontation.JobHandler;
import cn.keptdata.one2data.job.component.base.handler.AsyncJobHandler;
import cn.keptdata.one2data.job.component.base.handler.JobHandlerKeeper;
import cn.keptdata.one2data.job.component.base.handler.metadata.AsyncJobHandlerMetaData;
import cn.keptdata.one2data.job.component.base.info.JobInfo;
import cn.keptdata.one2data.job.mapper.JobPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 任务辅助工具类
 * @date 2021/8/26 14:03
 */
@Component
public class JobHelper {

    /**
     * 成功响应码标记(与API 响应码一致)
     */
    public static final Integer SUCCESS_CODE = 100000;
    /**
     * 任务进度信息存活时间
     */
    private static final Long JOB_PROGRESS_INFO_TTL_SECONDS = 10L;
    /**
     * 默认消息内容
     */
    private static final String DEFAULT_MESSAGE = "";
    /**
     * Redis 服务
     */
    private static RedisService redisService;
    /**
     * 消息总线服务
     */
    private static MessageBus messageBus;

    /**
     * @param data
     * @return cn.keptdata.one2data.core.api.response.Response<T>
     * @author Bob.Yang
     * @description 返回包含响应数据的成功返回消息 （消息体不支持字符串）
     * @date 2021/1/19 18:43
     **/
    public static <T> JobResult<T> success(T data) {
        return JobResult.<T>builder().success(true).code(SUCCESS_CODE).message(DEFAULT_MESSAGE).data(data).build();
    }

    /**
     * @return cn.keptdata.one2data.core.api.response.Response<T>
     * @author Bob.Yang
     * @description 返回包含响应数据的成功返回消息 （消息体不支持字符串）
     * @date 2021/1/19 18:43
     **/
    public static <T> JobResult<T> success() {
        return JobResult.<T>builder().success(true).code(SUCCESS_CODE).message(DEFAULT_MESSAGE).build();
    }

    /**
     * @param message
     * @param data
     * @return cn.keptdata.one2data.core.api.response.Response<T>
     * @author Bob.Yang
     * @description 返回包含成功消息提示和数据体的通用消息体
     * @date 2021/7/21 10:45
     */
    public static <T> JobResult<T> success(String message, T data) {
        return JobResult.<T>builder().success(true).code(SUCCESS_CODE).message(message).data(data).build();
    }

    /**
     * @param errorCode
     * @param errorMsg
     * @return cn.keptdata.one2data.core.api.response.Response
     * @author Bob.Yang
     * @description 根据指定的返回码和错误消息返回错误信息
     * @date 2021/1/15 18:42
     **/
    public static JobResult fail(Integer errorCode, String errorMsg) {
        return JobResult.builder().code(errorCode).message(errorMsg).build();
    }

    /**
     * @param failInfo
     * @return cn.keptdata.one2data.core.api.response.Response
     * @author Bob.Yang
     * @description 根据failInfo 生成失败信息
     * @date 2021/1/15 18:42
     **/
    public static JobResult fail(FailInfo failInfo) {
        return JobResult.builder().code(failInfo.getFailCode()).message(failInfo.getI18NMessage()).build();
    }

    /**
     * @param baseException 系统业务异常
     * @return cn.keptdata.one2data.core.api.response.Response
     * @author Bob.Yang
     * @description 根据业务异常返回封装好的响应消息
     * @date 2021/1/15 18:41
     **/
    public static JobResult fail(BaseException baseException) {
        return JobResult.builder().code(baseException.getCode()).message(baseException.getMessage()).build();
    }

    /**
     * @param jobHandlerClass
     * @return java.lang.String
     * @author Bob.Yang
     * @description 获取任务唯一标识符
     * @date 2021/8/26 16:08
     */
    public static <T extends BaseJobHandler> String getJobHandlerIdentifier(Class<T> jobHandlerClass) {
        return jobHandlerClass.getName();
    }

    /**
     * @param asyncJobHandlerMetaData
     * @return java.lang.String
     * @author Bob.Yang
     * @description 从异步任务元数据信息中获取任务名称（国际化）
     * @date 2021/8/27 15:47
     */
    public static String getJobI18NName(AsyncJobHandlerMetaData asyncJobHandlerMetaData) {

        JobHandler jobHandler = asyncJobHandlerMetaData.getJobHandler();

        // JOB 名称国际化编码
        String jobNameI18NKey = jobHandler.jobName();

        return i18NResource.getMessage(jobNameI18NKey);
    }

    /**
     * @param jobInfo
     * @param resource
     * @return void
     * @author Bob.Yang
     * @description 为任务附加之间关联的资源信息
     * @date 2021-12-25 14:29
     */
    public static <T extends Resource> void attachResource(JobInfo jobInfo, T resource) {

        jobInfo.setResourceUuid(resource.getResourceUuid());
        jobInfo.setResourceName(resource.getResourceName());
        jobInfo.setResourceType(resource.getResourceType());
    }

    /**
     * @param jobInfo
     * @param relatedResource
     * @return void
     * @author Bob.Yang
     * @description 为任务附加间接关联的资源信息
     * @date 2022-11-28 20:47
     */
    public static <T extends Resource> void attachRelatedResource(JobInfo jobInfo, T relatedResource) {
        jobInfo.setRelatedResourceUuid(relatedResource.getResourceUuid());
        jobInfo.setRelatedResourceName(relatedResource.getAdvanceResourceName());
        jobInfo.setRelatedResourceType(relatedResource.getResourceType());
    }

    /**
     * @param jobInfo
     * @param resource 直接关联的资源信息
     * @param relatedResource 间接关联的资源信息
     * @return void
     * @author Bob.Yang
     * @description 为任务附加直接和间接关联的资源信息
     * @date 2022-11-28 20:50
     */
    public static <T extends Resource, E extends Resource> void attachResources(JobInfo jobInfo, T resource,
        E relatedResource) {

        // 添加直接关联的资源信息
        attachResource(jobInfo, resource);
        // 添加间接关联的资源信息
        attachRelatedResource(jobInfo, relatedResource);
    }

    /**
     * @param jobUuid 任务唯一ID
     * @param progress 任务进度信息
     * @return void
     * @author Bob.Yang
     * @description 设置指定任务的进度信息
     * @date 2022-03-11 19:57
     */
    public static void setJobProgress(String jobUuid, int progress) {
        redisService.set(RedisKeyPrefix.JOB_PROGRESS.getKeyName(jobUuid), progress, JOB_PROGRESS_INFO_TTL_SECONDS);
    }

    /**
     * @param jobUuid 备份任务唯一ID
     * @return int
     * @author Bob.Yang
     * @description 获取指定备份任务的备份进度。（若获取不到，则返回0）
     * @date 2022-03-11 20:04
     */
    public static int getJobProgress(String jobUuid) {
        Object progress = redisService.get(RedisKeyPrefix.JOB_PROGRESS.getKeyName(jobUuid));
        if (Objects.nonNull(progress)) {
            return (int)progress;
        }

        return 0;
    }

    /**
     * @param job 异步任务信息
     * @return void
     * @author Bob.Yang
     * @description 推送任务最新的信息
     * @date 2022-03-14 11:55
     */
    public static void pushJobInfo(Job job) {
        JobPushInfo jobPushInfo = JobPushInfo.builder().jobUuid(job.getUuid()).detail(job.getDetail())
            .state(job.getState()).finishTime(job.getFinishTime()).build();
        messageBus.push(WsTopic.JOB_INFO, jobPushInfo);
    }

    /**
     * @param job 异步任务信息
     * @return void
     * @author PENGHUI
     * @description 推送任务最新的信息 状态变化
     * @date 2023-09-19 14:23
     */
    public static void pushJobState(Job job) {
        JobPushInfo jobState = JobPushInfo.builder().jobUuid(job.getUuid()).detail(job.getDetail())
            .state(job.getState()).finishTime(job.getFinishTime()).build();
        messageBus.push(WsTopic.JOB_STATE, jobState);
    }

    /**
     * @param jobUuid 任务唯一ID
     * @param progressPercent 任务百分比
     * @return void
     * @author Bob.Yang
     * @description 推送任务执行进度
     * @date 2022-03-14 11:58
     */
    public static void pushJobProgress(String jobUuid, int progressPercent) {
        JobProgressInfo jobProgressInfo = JobProgressInfo.builder().jobUuid(jobUuid).percent(progressPercent).build();
        messageBus.push(WsTopic.JOB_PROGRESS, jobProgressInfo);
    }

    /**
     * @param serviceModule
     * @param operateType
     * @return java.lang.Integer
     * @author yushijie
     * @description 获取任务平均耗时
     * @date 2023/7/18 15:02
     */
    public static Integer getJobAveragePeriod(ServiceModuleEnum serviceModule, OperateTypeEnum operateType) {
        JobPeriodMapper jobPeriodMapper = SpringContextUtil.getBean(JobPeriodMapper.class);
        JobPeriod jobPeriod =
            jobPeriodMapper.selectByServiceModuleAndOperateType(serviceModule.getCode(), operateType.getCode());
        if (Objects.isNull(jobPeriod)) {
            return -1;
        }
        return jobPeriod.getAveragePeriod() + 15;
    }

    /**
     * @param job
     * @return cn.keptdata.one2data.job.component.base.annontation.JobHandler
     * @author Bob.Yang
     * @description 获取指定任务的元数据信息
     * @date 2023-11-03 10:28
     */
    public static JobHandler getJobMetadata(Job job) {
        String jobHandlerIdentifier = job.getJobHandler();
        return JobHandlerKeeper.getJobMetadata(jobHandlerIdentifier);
    }

    /**
     * @param job
     * @return cn.keptdata.one2data.job.component.base.handler.AsyncJobHandler
     * @author Bob.Yang
     * @description 获取异步任务处理器
     * @date 2023-11-03 10:47
     */
    public static AsyncJobHandler getAsyncJobHandler(Job job) {
        String jobHandler = job.getJobHandler();
        return JobHandlerKeeper.getAsyncJobHandler(jobHandler);
    }

    @Autowired
    public void setI18NResource(I18NResource i18NResource) {
        JobHelper.i18NResource = i18NResource;
    }

    @Autowired
    public void setRedisService(RedisService redisService) {
        JobHelper.redisService = redisService;
    }

    @Autowired
    public void setMessageBus(MessageBus messageBus) {
        JobHelper.messageBus = messageBus;
    }

}
