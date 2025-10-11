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

package com.yushijie.common.backup;

import com.yushijie.common.base.ResourceEntity;
import com.yushijie.common.enumeration.ResourceType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description: 备份任务信息表
 * @date 2021/2/3 11:16
 */
@Data
public class BackupTask implements ResourceEntity, Serializable {
    private static final long serialVersionUID = 832682205990552651L;
    /**
     * @desc 备份任务UUID
     */
    private String uuid;

    private String tenantUuid;
    /**
     * @desc 备份任务名称
     */
    private String name;

    /**
     * @desc 任务关联的设备UUID
     */
    private String deviceUuid;

    /**
     * @desc 备份任务类型 @values disk_backup|file_backup|db_backup 磁盘备份|文件备份|数据库备份
     */
    private String taskType;

    /**
     * @desc 备份任务模式，参考 BackupTaskMode
     */
    private String taskMode;

    /**
     * @desc 当前备份任务是否自动选择存储LUN
     */
    private Boolean isAutoChooseStorageLun;

    /**
     * @desc 备份备份使用的存储服务UUID
     */
    private String storageUuid;

    /**
     * @desc 存储LUN UUID
     */
    private String storageLunUuid;

    /**
     * @desc 当前备份数据留存类型 @values time|number 按照数据保留时间|按照数据份数
     */
    private String dataRetentionType;

    /**
     * @desc 数据留存单位 @values day|month 天|月 @logical 当保留类型为：time时，当前值生效
     */
    private String dataRetentionUnit;

    /**
     * @desc 数据留存值 @logical 根据 数据留存类型，该字段有不同含义
     */
    private Integer dataRetentionValue;

    /**
     * @desc 数据持续保护类型 @values cdp | custom @logical 当类型为cdp 时：数据最小保障单位为1秒
     */
    private String cdpType;

    /**
     * @desc 数据持续保护最小单位 @values second|minute|hour|day 秒|分钟|小时|天
     */
    private String cdpIntervalUnit;

    /**
     * @desc 数据持续保护最小间隔值 @logical 该值与cdp_interval_unit 结合使用
     */
    private Integer cdpIntervalValue;

    /**
     * @desc 数据存储容量配额类型
     */
    private String dataStorageQuotaType;

    /**
     * @desc 数据存储容量配额参数
     */
    private String dataStorageQuotaParams;

    /**
     * @desc 备份策略描述信息
     */
    private String description;

    /**
     * @desc 是否限制备份引擎自动数据校验时间段
     */
    @Deprecated
    private String isLimitDataCheckTime;

    /**
     * @desc 数据校验可执行时间段--开始时间
     */
    @Deprecated
    private String enableDatacheckBeginTime;

    /**
     * @desc 数据校验可执行时间段--结束时间
     */
    @Deprecated
    private String enableDatacheckEndTime;

    /**
     * @desc 数据校验间隔周期单位，
     */
    private String datacheckIntervalUnit;

    /**
     * @desc 数据校验间隔周期值
     */
    private Integer datacheckIntervalValue;

    /**
     * @desc 是否启用定期数据校验功能
     */
    private String isEnableScheduleDatacheck;

    /**
     * @desc 是否是AI自动校验
     */
    private String isAiAutoCheck;

    /**
     * @desc 是否是AI自动提取
     */
    private String isAiAutoSnap;

    /**
     * @desc ai窗口大小
     */
    private Integer aiWindow;

    /**
     * @desc 若启用定期数据校验，定期校验方式
     */
    private String datacheckScheduleType;

    /**
     * @desc 若以cron 方式启动定期校验，cron 的配置信息
     */
    private String datacheckScheduleCron;

    /**
     * @desc 快照间隔周期单位，
     */
    private String snapshotIntervalUnit;

    /**
     * @desc 快照间隔周期值
     */
    private Integer snapshotIntervalValue;

    /**
     * @desc 是否强制同步驱动缓存数据
     */
    private String isForceSyncClean;

    /**
     * @desc 同步前是否刷缓存
     */
    private String isSyncFlushCache;

    /**
     * @desc 是否启用精简备份
     */
    private String isEnableLiteBackup;

    /**
     * @desc 是否限制备份任务执行时间
     */
    private String isLimitBackupTime;

    /**
     * @desc 当用户启用限制备份时间段时，备份可执行开始时间
     */
    private String enableBackupBeginTime;

    /**
     * @desc 当用户启用限制备份时间段时，备份可执行结束时间
     */
    private String enableBackupEndTime;

    /**
     * @desc 是否限制备份速度
     */
    private String isLimitBackupSpeed;

    /**
     * @desc 备份速度限速单位
     */
    private String speedLimitUnit;

    /**
     * @desc 备份速度限速值
     */
    private Integer speedLimitValue;

    /**
     * @desc 是否限制磁盘速度
     */
    private String isLimitDiskSpeed;

    /**
     * @desc 磁盘速度限速单位
     */
    private String diskLimitUnit;

    /**
     * @desc 磁盘速度限速值
     */
    private Integer diskLimitValue;

    /**
     * @desc 是否启用备份优先
     */
    private String isEnableBackupPriority;

    /**
     * @desc 备份过程中对原机存储性能最大影响率
     */
    private Integer srcDeviceStoreOccupyRate;

    /**
     * @desc 是否启用数据重删功能
     */
    private String isEnableDataDeduplication;

    /**
     * @desc 备份粒度
     */
    private String backupGranularity;

    /**
     * @desc 备份任务综合状态
     */
    private String state;

    /**
     * 备份任务运行状态 @values:BackupRunState
     */
    private String runState;

    /**
     * @desc 备份引擎服务状态 @values BackupEngineState
     */
    private String backupEngineState;

    /**
     * @desc 备份引擎工作状态 @values backupEngineWorkState
     */
    private String backupEngineWorkState;

    /**
     * @desc 备份任务最新工作时间点
     */
    private Date latestWorkTime;

    /**
     * @desc 备份任务最新工作信息码
     */
    private Integer backupCode;

    /**
     * @desc 备份任务最新工作信息
     */
    private String backupInfo;

    /**
     * @desc 任务脚本
     */
    private String taskScript;

    /**
     * @desc 当前配置是否为裸配置。（原始配置，即用户未配置过）
     */

    private String isRawConfig;

    /**
     * @desc 当前备份任务是否在线
     */
    private String isOnline;

    /**
     * @desc 备份任务使用的备份策略模板唯一ID
     */
    private String policyUuid;

    /**
     * @desc 当前备份任务使用的备份策略名称（默认与模板一致）
     */
    private String policyName;

    /**
     * @desc 当前备份任务使用的是否为原始的策略模板 （用户是否自定义过备份配置信息）
     */
    private String isOriginalPolicy;

    private String isCluster;

    /**
     * @desc 是否加密
     */
    private String isEncrypt;
    /**
     * @desc 加密类型
     */
    private String encryptType;
    /**
     * @desc 是否压缩
     */
    private String isCompress;
    /**
     * @desc 压缩类型
     */
    private String compressType;
    /**
     * @desc 压缩级别
     */
    private String compressLevel;

    /**
     * @desc 备份任务关联的设备磁盘原始JSON 结构 (在备份任务的生命周期中不做更新)
     */
    private String deviceDiskInfo;

    /**
     * @desc 备份任务关联的设备逻辑卷原始JSON 结构 (在备份任务的生命周期中不做更新)
     */
    private String deviceVolumeInfo;

    /**
     * @desc 任务创建者
     */
    private String createUser;

    /**
     * @desc 任务修改者
     */
    private String updateUser;

    /**
     * @desc 创建时间
     */
    private Date createTime;

    /**
     * @desc 修改时间
     */
    private Date updateTime;

    @Override
    public String getResourceUuid() {
        return this.uuid;
    }

    @Override
    public String getResourceType() {
        return ResourceType.BACKUP_TASK.getCode();
    }

    @Override
    public String getResourceName() {
        return this.name;
    }
}