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

package com.yushijie.common.core.messagebus.websocket.common;

import cn.keptdata.one2data.header.base.enumeration.BaseEnum;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description WebSocket 主题枚举
 * @date 2021/7/23 22:35
 */
public enum WsTopic implements BaseEnum {

    // *******************备份模块相关***********************

    /**
     * 备份任务流量监控
     */
    BACKUP_TASK_TRAFFIC_MONITOR("backupTaskTrafficMonitor"),

    /**
     * 备份任务状态监控
     */
    BACKUP_TASK_STATE_MONITOR("backupTaskStateMonitor"),

    /**
     * 备份任务信息监控
     */
    BACKUP_TASK_INFO_MONITOR("backupTaskInfoMonitor"),

    /**
     * 备份任务状态检测开始时间信息推送频道
     */
    BACKUP_TASK_OFFLINE_STATUS_MONITOR("backupTaskOfflineStatusMonitor"),

    /**
     * 备份任务总进度
     */
    BACKUP_TASK_TOTAL_PROGRESS("backupTaskTotalProgress"),

    /**
     * 备份详情页-硬件备份状态
     */
    HARDWARE_BACKUP_STATE("hardwareBackupState"),

    /**
     * 硬件备份进度
     */
    HARDWARE_BACKUP_PROGRESS("hardwareBackupProgress"),

    /**
     * 订阅进度推送topic(实际使用会拼接资源类型，如：subscribedProgressPush/backupTask)
     */
    SUBSCRIBED_PROGRESS_PUSH("subscribedProgressPush/"),
    /**
     * 日志收集状态信息推送
     */
    LOG_COLLECT_STATE_INFO_PUSH("logCollectStateInfoPush/"),
    /**
     * 登录态过期
     */
    EXPIRED_SIGN_IN("signInExpired"),

    /**
     * 订阅进度topic
     */
    PROCESS_SUBSCRIBE("processSubscribe"),
    /**
     * 备份任务数据变更
     */
    BACKUP_TASK_DATA_CHANGE("backupTaskDataChange"),

    CONFIG_CHANGE_MESSAGE_PUSH("configChangeMessagePush/"),
    /**
     * 集群备份任务数据变更
     */
    CLUSTER_BACKUP_TASK_DATA_CHANGE("clusterBackupTaskDataChange"),

    // *******************恢复模块相关***********************

    /**
     * 恢复任务-整体进度信息
     */
    RECOVERY_TASK_PROGRESS_INFO("recoveryTaskProgress"),

    /**
     * 恢复目标端状态
     */
    RECOVERY_TARGET_DEVICE_STATE("recoveryTargetDeviceState"),

    /**
     * 恢复目标端（已安装恢复服务）数据变更
     */
    RECOVERY_TARGET_DATA_CHANGE("recoveryTargetDataChange"),

    /**
     * 恢复目标端（待安装恢复服务）数据变更
     */
    READY_INSTALL_RECOVERY_TARGET_DATA_CHANGE("readyInstallRecoveryTargetDataChange"),

    /**
     * 恢复任务-整体状态信息
     */
    RECOVERY_TASK_STATE_INFO("recoveryTaskState"),

    /**
     * 恢复任务-最新恢复时间
     */
    RECOVERY_LATEST_DATA_POINT("recoveryLatestDataPoint"),

    /**
     * 恢复任务-备注信息 ->描述
     */
    RECOVERY_TASK_DESCRIPTION_INFO("recoveryTaskDescription"),

    /**
     * 恢复任务硬件恢复状态信息
     */
    HARDWARE_RECOVERY_STATE("hardwareRecoveryState"),

    /**
     * 恢复任务硬件恢复进度信息
     */
    HARDWARE_RECOVERY_PROGRESS("hardwareRecoveryProgress"),

    /**
     * 恢复任务数据变更
     */
    RECOVERY_TASK_DATA_CHANGE("recoveryTaskDataChange"),

    /**
     * 恢复任务流量推送
     */
    RECOVERY_TASK_TRAFFIC("recoveryTaskTraffic"),

    /**
     * 文件恢复任务数据变更
     */
    FILE_RECOVERY_TASK_DATA_CHANGE("fileRecoveryTaskDataChange"),

    /**
     * 文件恢复任务-整体状态信息
     */
    FILE_RECOVERY_TASK_STATE_INFO("fileRecoveryTaskState"),

    /**
     * 文件恢复任务-备注信息 ->描述
     */
    FILE_RECOVERY_TASK_DESCRIPTION_INFO("fileRecoveryTaskDescription"),

    // *******************挂载功能模块相关*******************

    /**
     * 挂载任务数据监控
     */
    MOUNT_TASK_INFO_MONITOR("mountTaskInfoMonitor"),

    /**
     * 挂载任务组状态监控
     */
    MOUNT_TASK_GROUP_INFO_MONITOR("mountTaskGroupInfoMonitor"),

    /**
     * 挂载服务状态
     */
    INITIATOR_STATE("initiatorState"),

    // *******************容灾模块相关***********************

    /**
     * 接管主机状态监控
     */
    TAKEOVER_STATE_MONITOR("takeoverStateMonitor"),

    /**
     * 接管主机数据变更
     */
    TAKEOVER_DATA_CHANGE("takeoverDataChange"),

    /**
     * 主备切换计划状态监控
     */
    TAKEOVER_PLAN_STATE_MONITOR("takeoverPlanStateMonitor"),

    // *******************验证模块相关***********************

    /**
     * 验证主机状态监控
     */
    DATA_VERIFY_STATE_MONITOR("dataVerifyStateMonitor"),

    /**
     * 验证主机数据变更
     */
    DATA_VERIFY_DATA_CHANGE("dataVerifyDataChange"),

    /**
     * 验证主机外网访问ip变更
     */
    DATA_VERIFY_INSTANCE_EIP_CHANGE("dataVerifyInstanceEipChange"),

    // *******************客户端管理相关***********************

    /**
     * 客户端安装信息
     */
    CLIENT_INSTALL_MESSAGE("ClientInstallMessage"),

    /**
     * 备份客户端状态监控
     */
    CLIENT_STATE_MONITOR("clientStateMonitor"),

    /**
     * 恢复客户端安装信息
     */
    RECOVERY_CLIENT_INSTALL_INFO("recoveryClientInstallInfo"),

    /**
     * 客户端扫描添加结果
     */
    SCAN_RESULT("scanResult"),

    /**
     * 已安装客户端数据变更
     */
    CLIENT_DATA_CHANGE("clientDataChange"),
    /**
     * 客户端启动项变更
     */
    BOOTLOADER_DATA_CHANGE("bootloaderDataChange"),

    // *******************容灾资源相关***********************

    /**
     * 计算节点状态变更
     */
    COMPUTE_STATE_MONITOR("computeStateMonitor"),

    /**
     * 存储节点安装状态信息
     */
    STORAGE_INSTALL_MESSAGE("storageInstallMessage"),

    /**
     * 存储节点状态推送
     */
    STORAGE_STATE_MONITOR("storageStateMonitor"),

    /**
     * 存储LUN状态推送
     */
    STORAGE_LUN_STATE_MONITOR("storageLunStateMonitor"),

    /**
     * 计算节点数据变更
     */
    COMPUTE_DATA_CHANGE("computeDataChange"),

    /**
     * 存储节点数据变更
     */
    STORAGE_DATA_CHANGE("storageDataChange"),

    /**
     * 存储LUN数据变更推送
     */
    STORAGE_LUN_DATA_CHANGE("storageLunDataChange"),

    // *******************运维模块相关***********************

    /**
     * 任务中心-任务状态变更
     */
    JOB_INFO("jobInfo"),

    /**
     * 运维管理任务中心-任务状态变更 和任务中心弹窗区分开
     */
    JOB_STATE("jobState"),

    /**
     * 任务中心-任务的进度
     */
    JOB_PROGRESS("jobProgress"),

    /**
     * 任务中心-任务统计信息
     */
    JOB_STATISTICS("jobStatistics"),

    /**
     * 授权中心-授权信息
     */
    LICENSE("license"),

    /**
     * 消息中心
     */
    NOTICE("notice"),

    /**
     * 审批提醒
     */
    APPROVE("approve"),

    /**
     * 未读数量 目前是admin未读数量
     */
    NOTICE_UNREAD("noticeUnread"),

    /**
     * 待安装客户端数据变化通知
     */
    CLIENT_INSTALL_DATA_CHANGE("clientInstallDataChange"),

    /**
     * 步骤详情
     */
    PROGRESS_DETAIL("progressDetail"),

    /**
     * 主备切换计划数据变更通知
     */
    TAKEOVER_PLAN_DATA_CHANGE("takeoverPlanDataChange"),

    /**
     * 平台设置数据变更
     */
    ONE_CONFIG_DATA_CHANGE("oneConfigDataChange"),

    /**
     * 平台设置备份数据库时间推送
     */
    ONE_CONFIG_BACKUP_DATABASE_TIME("oneConfigBackupDatabaseTime"),

    /**
     * 扫描IP单个进度
     */
    SCAN_IP_SINGLE_PROGRESS("scanIpSingleProgress"),

    /**
     * 扫描IP整体进度
     */
    SCAN_IP_ALL_PROGRESS("scanIpAllProgress"),

    // *******************远程复制相关***********************

    /**
     * 远程复制任务状态
     */
    REPLICATION_TASK_STATE("replicationTaskState"),

    /**
     * 远程复制任务进度和速度信息
     */
    REPLICATION_TASK_PROGRESS_INFO("replicationTaskProgressInfo"),

    // *******************巡检模块相关***********************

    /**
     * 巡检模块状态
     */
    INSPECT_MODULE_STATE("inspectModuleState"),

    /**
     * 巡检总状态
     */
    INSPECT_TASK_STATE("inspectTaskState"),

    // *******************云容灾相关***********************

    /**
     * 云存储池状态
     */
    CLOUD_DATASTORE_STATE("cloudDatastoreState"),
    /**
     * 云存储池数据变更
     */
    CLOUD_DATASTORE_DATA_CHANGE("cloudDatastoreDataChange"),

    /**
     * 云平台虚拟机机状态监控
     */
    CLOUDVM_STATE_MONITOR("cloudVMStateMonitor"),

    /**
     * 云平台镜像状态监控
     */
    CLOUD_IMAGE_STATE_MONITOR("cloudImageStateMonitor"),

    /**
     * 平台升级-检测信息
     */
    PLATFORM_UPGRADE_CHECK("platformUpgradeCheck"),

    PLATFORM_MAINTENANCE_STATE("platformMaintenanceState"),

    /**
     * 平台升级-维护进度
     */
    PLATFORM_MAINTENANCE_PROGRESS("platformMaintenanceProgress"),
    /**
     * 平台升级-升级状态
     */
    PLATFORM_UPGRADE_STATE("platformUpgradeState"),
    /**
     * 补丁升级数据变更推送
     */
    PATCH_UPGRADE_DATA_CHANGE("patchUpgradeDataChange"),

    /**
     * 归档日志合并任务进度
     */
    ARCHIVED_LOG_MERGE_EXPORT_TASK("archivedLogMergeExportTask"),
    ;

    private final String topic;

    WsTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
