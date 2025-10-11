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

package com.yushijie.common.enumeration;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 系统资源类型枚举
 * @date 2021/5/19 18:23
 */
public enum ResourceType implements BaseEnum<ResourceType> {

    BACKUP_TASK("backupTask", "备份任务", "common.resource.backupTask"),

    CLUSTER_BACKUP_TASK("clusterBackupTask", "集群备份任务", "common.resource.clusterBackupTask"),

    RECOVERY_TASK("recoveryTask", "容灾恢复任务", "common.resource.recoveryTask"),

    FILE_RECOVERY_TASK("fileRecoveryTask", "文件恢复任务", "common.resource.fileRecoveryTask"),

    BACKUP_POLICY("backupPolicy", "备份策略", "common.resource.backupPolicy"),

    BACKUP_DATA_GARBAGE("backupDataGarbage", "备份垃圾文件", "common.resource.backupDataGarbage"),

    COMPUTE("compute", "计算节点", "common.resource.compute"),

    COMPUTE_NIC("computeNic", "计算节点网卡", "common.resource.computeNic"),

    STORAGE("storage", "存储节点", "common.resource.storage"),

    STORAGE_LUN("storageLun", "存储LUN", "common.resource.storageLun"),

    BACKUP_CLIENT("backupClient", "备份客户端", "common.resource.backupClient"),

    BACKUP_CLIENT_TO_GUARANTEE("backupClientToGuarantee", "备份客户端-待保障", "common.resource.backupClientToGuarantee"),

    BACKUP_CLIENT_CLUSTER("backupClientCluster", "备份客户端集群", "common.resource.backupClientCluster"),

    BACKUP_CLIENT_CLUSTER_TO_GUARANTEE("backupClientClusterToGuarantee", "备份客户端集群-待保障",
        "common.resource.backupClientClusterToGuarantee"),

    BACKUP_DRIVER("backupDriver", "备份驱动", "common.resource.backupDriver"),

    RECOVERY_TARGET("recoveryTarget", "恢复目标机", "common.resource.recoveryTarget"),

    DATA_VIEW_MOUNT("dataViewMount", "数据浏览挂载", "common.resource.dataViewMount"),

    DATA_VIEW_MOUNT_GROUP("dataViewMountGroup", "数据库浏览挂载组", "common.resource.dataViewMountGroup"),

    TAKEOVER_PLAN("takeoverPlan", "主备切换计划", "common.resource.takeoverPlan"),

    TAKEOVER_INSTANCE("takeoverInstance", "接管主机", "common.resource.takeoverInstance"),

    VERIFY_INSTANCE("verifyInstance", "验证主机", "common.resource.verifyInstance"),

    EIP("eip", "eip", "common.resource.eip"),

    NETWORK("network", "网络", "common.resource.network"),

    VERIFY_NET("verifyNet", "验证网络", "common.resource.verifyNet"),

    TAKEOVER_NET("takeoverNet", "接管网络", "common.resource.takeoverNet"),

    APPROVE("approve", "审批中心", "common.resource.approve"),

    ROLE("role", "角色", "common.resource.role"),

    USER("user", "用户", "common.resource.user"),

    TENANT("tenant", "租户", "common.resource.tenant"),

    REMOTE_REPLICATION_TASK("remoteReplicationTask", "单机远程复制任务", "common.resource.remoteReplicationTask"),

    CLUSTER_REMOTE_REPLICATION_TASK("clusterRemoteReplicationTask", "集群远程复制任务",
        "common.resource.clusterRemoteReplicationTask"),

    REMOTE_REPLICATION_POLICY("remoteReplicationPolicy", "远程复制策略", "common.resource.remoteReplicationPolicy"),

    INSPECT("inspect", "巡检任务", "common.resource.inspect"),

    CLOUD_ACCOUNT("cloudAccount", "云账户", "common.resource.cloudAccount"),

    CLOUD_DATASTORE("cloudDatastore", "云存储器", "common.resource.cloudDatastore"),

    CLOUD_IMAGE("cloudImage", "云镜像", "common.resource.cloudImage"),

    CLOUD_INSTANCE("cloudInstance", "云目标虚拟机", "common.resource.cloudInstance"),

    CLOUD_HOST("cloudHost", "云主机", "common.resource.cloudHost"),

    CLOUD_PORT_GROUP("cloudPortGroup", "云端口组", "common.resource.cloudPortGroup"),

    CLOUD_PROVIDER("cloudProvider", "云服务商", "common.resource.cloudProvider"),

    CLOUD_VIRTUAL_SWITCH("cloudVSwitch", "云虚拟交换机", "common.resource.cloudVSwitch"),

    CLOUD_NET_POLICY("cloudNetPolicy", "云网络策略", "common.resource.cloudNetPolicy"),

    PLATFORM_UPGRADE("platformUpgrade", "平台升级任务", "common.resource.platformUpgrade"),

    LOG_ARCHIVE("logArchive", "归档日志", "common.resource.logArchive"),

    LOG_ARCHIVE_MERGE_TASK("logArchiveMergeTask", "归档日志合并任务", "common.resource.logArchiveMergeTask"),

    LOG_DISASTER_RECOVERY("logDisasterRecovery", "容灾日志", "common.resource.logDisasterRecovery"),

    UNDEFINED("undefined", "未定义类型", "common.resource.undefined");

    /**
     * @desc 资源类型编码
     */
    private final String type;

    private final String desc;

    private final String i18nCode;

    ResourceType(String type, String desc, String i18Code) {
        this.type = type;
        this.desc = desc;
        this.i18nCode = i18Code;
    }

    /**
     * @author PENGHUI
     * @description 获取组件枚举
     * @date 2024/5/15 11:34
     * @param type
     * @return cn.keptdata.one2data.header.common.enumeration.ResourceType
     **/
    public static ResourceType getComponentResourceTypeByType(String type) {
        if (type.equals(BACKUP_CLIENT.type)) {
            return BACKUP_CLIENT;
        }
        if (type.equals(COMPUTE.type)) {
            return COMPUTE;
        }
        if (type.equals(STORAGE.type)) {
            return STORAGE;
        }
        return null;
    }

    public static String getResourceTypeI18nCodeByType(String type) {
        for (ResourceType value : ResourceType.values()) {
            if (type.equals(value.getCode())) {
                return value.getI18nCode();
            }
        }
        return "";
    }

    @Override
    public String getCode() {
        return this.type;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    public String getI18nCode() {
        return i18nCode;
    }
}
