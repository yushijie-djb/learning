package com.yushijie.common.protobuf.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import cn.keptdata.logging.Log;
import cn.keptdata.logging.LogFactory;
import cn.keptdata.one2data.header.message.protobuf.auth.Auth;
import cn.keptdata.one2data.header.message.protobuf.client.BackupCommand;
import cn.keptdata.one2data.header.message.protobuf.client.BackupMessage;
import cn.keptdata.one2data.header.message.protobuf.client.ClientMessage;
import cn.keptdata.one2data.header.message.protobuf.common.Common;
import cn.keptdata.one2data.header.message.protobuf.common.Node;
import cn.keptdata.one2data.header.message.protobuf.compute.ComputeMessage;
import cn.keptdata.one2data.header.message.protobuf.initiator.InitiatorIscsi;
import cn.keptdata.one2data.header.message.protobuf.initiator.InitiatorMessage;
import cn.keptdata.one2data.header.message.protobuf.initiator.InitiatorMount;
import cn.keptdata.one2data.header.message.protobuf.initiator.InitiatorSamba;
import cn.keptdata.one2data.header.message.protobuf.pe.PEMessage;
import cn.keptdata.one2data.header.message.protobuf.pe.RecoveryCommand;
import cn.keptdata.one2data.header.message.protobuf.pe.RecoveryMessage;
import cn.keptdata.one2data.header.message.protobuf.remote.RemoteSync;
import cn.keptdata.one2data.header.message.protobuf.storage.BackendCommand;
import cn.keptdata.one2data.header.message.protobuf.storage.BackendProcessMonitor;
import cn.keptdata.one2data.header.message.protobuf.storage.ControlAction;
import cn.keptdata.one2data.header.message.protobuf.storage.ControlBatch;
import cn.keptdata.one2data.header.message.protobuf.storage.ControlCommon;
import cn.keptdata.one2data.header.message.protobuf.storage.ControlMulti;
import cn.keptdata.one2data.header.message.protobuf.tool.LogUploadMessage;
import cn.keptdata.one2data.header.message.protobuf.upgrade.UpgradeMessage;
import cn.keptdata.one2data.header.message.protobuf.vmagent.VMAgent;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description ProtoBuf 格式化工具类
 * @date 2021-12-25 17:38
 */
public class ProtoFormatter {

    private static final Log log = LogFactory.getLog();

    /**
     * JSON 格式化输出器
     */
    private static JsonFormat.Printer jsonFormatPrinter = null;

    static {
        // ProtoBuf 泛型类型集合
        List<Descriptors.Descriptor> anyFieldDescriptors = new ArrayList<>();

        /**
         * Common
         */
        anyFieldDescriptors.add(Common.Message.getDescriptor());
        anyFieldDescriptors.add(Common.Header.getDescriptor());
        anyFieldDescriptors.add(Common.BaseAck.getDescriptor());

        anyFieldDescriptors.add(Node.NodeBaseInfo.getDescriptor());
        anyFieldDescriptors.add(Node.NetCardInfo.getDescriptor());
        anyFieldDescriptors.add(Node.BootLoader.getDescriptor());
        anyFieldDescriptors.add(Node.BlockDeviceKey.getDescriptor());
        anyFieldDescriptors.add(Common.NetworkInfo.getDescriptor());
        anyFieldDescriptors.add(Common.NetworkInfo.ConnInfo.getDescriptor());

        /**
         * Auth
         */
        anyFieldDescriptors.add(Auth.ImportLicense.getDescriptor());
        anyFieldDescriptors.add(Auth.ImportLicenseAck.getDescriptor());
        anyFieldDescriptors.add(Auth.QueryLicenseDetail.getDescriptor());
        anyFieldDescriptors.add(Auth.QueryLicenseDetailAck.getDescriptor());
        anyFieldDescriptors.add(Auth.ApplyNodeLicense.getDescriptor());
        anyFieldDescriptors.add(Auth.ApplyNodeLicenseAck.getDescriptor());
        anyFieldDescriptors.add(Auth.CorrectNodeLicense.getDescriptor());
        anyFieldDescriptors.add(Auth.CorrectNodeLicenseAck.getDescriptor());
        anyFieldDescriptors.add(Auth.VerifyLicense.getDescriptor());
        anyFieldDescriptors.add(Auth.VerifyLicenseAck.getDescriptor());
        anyFieldDescriptors.add(Auth.ReportLicenseExpire.getDescriptor());
        anyFieldDescriptors.add(Auth.ApplyFunctionLicense.getDescriptor());
        anyFieldDescriptors.add(Auth.RevokeFunctionLicense.getDescriptor());

        /**
         * Client
         */
        anyFieldDescriptors.add(BackupCommand.Action.getDescriptor());
        anyFieldDescriptors.add(BackupCommand.ActionAck.getDescriptor());

        anyFieldDescriptors.add(ClientMessage.Login.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.LoginAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.AcquireNodeAndHardwareInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.AcquireNodeAndHardwareInfoAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.HardwareInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.NodeAndHardwareInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.ReportNodeAndHardwareInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.ReportNodeAndHardwareInfoAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.ReportHardwareChanged.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.HarddiskInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.PartitionInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.LVInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.LVInfo.LVRefDevice.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupWorkStateReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupStateReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupStateReport.HardwareBackupState.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupProgress.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupProgress.HardwareBackupProgress.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.NoEnoughAuthorizedLunCapacityReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.SyncIndexInfoReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.SyncIndexInfoReport.FineGrainedSyncIndexInfo.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.BackupServiceReadyReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.RequestDeviceMetadata.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.RequestDeviceMetadataAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.VerifyNodeInfoReport.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.VerifyNodeInfoReportAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.RequestDeviceClusterType.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.RequestDeviceClusterTypeAck.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.ResetAgentEnvironment.getDescriptor());
        anyFieldDescriptors.add(ClientMessage.RestartAgentService.getDescriptor());

        anyFieldDescriptors.add(BackupMessage.BackupConfig.ExtendParameter.getDescriptor());
        anyFieldDescriptors.add(BackupMessage.BackupConfig.getDescriptor());
        anyFieldDescriptors.add(BackupMessage.BackupConfig.NeedBackupHardware.getDescriptor());
        anyFieldDescriptors.add(BackupMessage.SyncIndexInfo.getDescriptor());
        anyFieldDescriptors.add(BackupMessage.SubscribeBackupProgress.getDescriptor());
        anyFieldDescriptors.add(BackupMessage.UnSubscribeBackupProgress.getDescriptor());

        /**
         * PE Recovery
         */
        anyFieldDescriptors.add(PEMessage.Login.getDescriptor());
        anyFieldDescriptors.add(PEMessage.LoginAck.getDescriptor());
        anyFieldDescriptors.add(PEMessage.ReportNodeInfo.getDescriptor());
        anyFieldDescriptors.add(PEMessage.ReportNodeInfoAck.getDescriptor());
        anyFieldDescriptors.add(PEMessage.HarddiskInfo.getDescriptor());
        anyFieldDescriptors.add(PEMessage.StartRecoveryClientInstall.getDescriptor());
        anyFieldDescriptors.add(PEMessage.RebootOS.getDescriptor());
        anyFieldDescriptors.add(PEMessage.RebootOSAck.getDescriptor());

        anyFieldDescriptors.add(RecoveryCommand.Action.getDescriptor());
        anyFieldDescriptors.add(RecoveryCommand.ActionAck.getDescriptor());
        anyFieldDescriptors.add(RecoveryCommand.Qcow2AndTargetHardwareMapping.getDescriptor());
        anyFieldDescriptors.add(RecoveryCommand.BoxAndTargetHardwareMapping.getDescriptor());

        anyFieldDescriptors.add(RecoveryMessage.RecoveryConfig.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryConfig.NeedRecoveryHardware.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryWorkStateReport.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryStateReport.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryStateReport.HardwareRecoveryState.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryProgress.getDescriptor());
        anyFieldDescriptors.add(RecoveryMessage.RecoveryProgress.HardwareRecoveryProgress.getDescriptor());

        /**
         * storage
         */
        anyFieldDescriptors.add(ControlAction.ActionCreateQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionDeleteQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionRenameQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionReBackingQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionMergeQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionBlockCommitQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionBlockPullQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionCreateBox.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionDeleteBox.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionMergeBoxToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlAction.Qcow2Node.getDescriptor());
        anyFieldDescriptors.add(ControlAction.ActionRebuildQcow2Chains.getDescriptor());

        anyFieldDescriptors.add(ControlBatch.BatchJobQuery.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchJobQueryAck.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchJobInfo.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchJobCmd.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchJobCmdAck.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchJobSubmitAck.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateBaseQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateBaseQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateRealtimeQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateRealtimeQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateDiffQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateDiffQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateSnapQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateSnapQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateRelayAndAppQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateRelayAndAppQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateAppQcow2DependRealtimeQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateAppQcow2DependRealtimeQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateAppQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchCreateAppQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchMergeQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchMergeQcow2ChainsToQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchBlockCommitQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchBlockCommitQcow2ChainsToQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchBlockPullQcow2ChainsToQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchBlockPullQcow2ChainsToQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteAnyQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteAnyQcow2Report.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteAnyBox.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteAnyBoxReport.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteQcow2AndBox.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchDeleteQcow2AndBoxReport.getDescriptor());

        anyFieldDescriptors.add(ControlBatch.BatchSwapRealtimeQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchSwapRealtimeQcow2.Actions.getDescriptor());
        anyFieldDescriptors.add(ControlBatch.BatchSwapRealtimeQcow2Report.getDescriptor());

        anyFieldDescriptors.add(ControlMulti.MultiCreateAppQcow2.getDescriptor());
        anyFieldDescriptors.add(ControlMulti.MultiCreateRelayAndAppQcow2.getDescriptor());

        anyFieldDescriptors.add(ControlCommon.Login.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.LoginAck.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.ReportNodeInfo.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.ReportNodeInfoAck.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.AssignSpacePath.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.AssignSpacePathAck.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.DeleteFileDirectory.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.DeleteFileDirectoryAck.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.FileDirectoryInfoQuery.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.FileDirectoryInfoQueryAck.getDescriptor());
        anyFieldDescriptors.add(ControlCommon.ReportJobRunInfo.getDescriptor());

        anyFieldDescriptors.add(RemoteSync.RemoteSyncSubmitAck.getDescriptor());
        anyFieldDescriptors.add(RemoteSync.RemoteSyncStateProgressReport.getDescriptor());
        anyFieldDescriptors.add(RemoteSync.RemoteSyncStart.getDescriptor());
        anyFieldDescriptors.add(RemoteSync.RemoteSyncStart.Info.getDescriptor());
        anyFieldDescriptors.add(RemoteSync.RemoteSyncStop.getDescriptor());
        anyFieldDescriptors.add(RemoteSync.RemoteSyncIdxReport.getDescriptor());

        anyFieldDescriptors.add(BackendCommand.NfsMount.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsMountAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsUmount.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsUmountAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiLogin.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiLoginAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiLogout.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiLogoutAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.BlockDeviceFormat.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.BlockDeviceFormatAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.FcScan.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.FcScanAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.FcRemove.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.FcRemoveAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsCreateShared.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsCreateSharedAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsRemoveShared.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.NfsRemoveSharedAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.BlockDeviceUmount.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.BlockDeviceUmountAck.getDescriptor());

        anyFieldDescriptors.add(BackendCommand.QueryDirAllFileInfo.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.QueryDirAllFileInfoAck.getDescriptor());
        /**
         * 存储服务状态订阅
         */
        anyFieldDescriptors.add(BackendProcessMonitor.SubProcessState.getDescriptor());
        anyFieldDescriptors.add(BackendProcessMonitor.ReportProcessState.getDescriptor());

        /**
         * 文件浏览挂载
         */
        anyFieldDescriptors.add(BackendCommand.IscsiJobSubmitAck.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiNewTargetLun.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiNewTargetLun.LunInfo.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiDeleteTargetLun.getDescriptor());
        anyFieldDescriptors.add(BackendCommand.IscsiUpdateTargetAcl.getDescriptor());

        anyFieldDescriptors.add(InitiatorIscsi.IscsiLogin.getDescriptor());
        anyFieldDescriptors.add(InitiatorIscsi.IscsiLoginAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorIscsi.IscsiLoginAck.DiskInfo.getDescriptor());
        anyFieldDescriptors.add(InitiatorIscsi.IscsiLogout.getDescriptor());
        anyFieldDescriptors.add(InitiatorIscsi.IscsiLogoutAck.getDescriptor());

        anyFieldDescriptors.add(InitiatorMount.BuildBlockDevice.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.BuildBlockDevice.AdaptDiskInfo.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.BuildBlockDeviceAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.BuildBlockDeviceAck.AdaptBlockDeviceInfo.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.MountFileSystem.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.MountFileSystem.Arguments.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.MountFileSystemAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.UmountFileSystem.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.UmountFileSystem.Arguments.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.UmountFileSystemAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorMount.RemoveMountDir.getDescriptor());
        anyFieldDescriptors.add(InitiatorMessage.ReportNodeInfo.getDescriptor());
        anyFieldDescriptors.add(InitiatorMessage.ReportNodeInfoAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorSamba.SambaMake.getDescriptor());
        anyFieldDescriptors.add(InitiatorSamba.SambaMakeAck.getDescriptor());
        anyFieldDescriptors.add(InitiatorSamba.SambaRemove.getDescriptor());
        anyFieldDescriptors.add(InitiatorSamba.SambaRemoveAck.getDescriptor());

        /**
         * upgrade
         */
        anyFieldDescriptors.add(UpgradeMessage.StartNodeUpgrade.getDescriptor());
        anyFieldDescriptors.add(UpgradeMessage.StartNodeUpgradeAck.getDescriptor());
        anyFieldDescriptors.add(UpgradeMessage.UpgradeLogin.getDescriptor());
        anyFieldDescriptors.add(UpgradeMessage.UpgradeLoginAck.getDescriptor());
        anyFieldDescriptors.add(UpgradeMessage.ReportUpgradeInfo.getDescriptor());
        /**
         * logCollect
         */
        anyFieldDescriptors.add(LogUploadMessage.LogUploadLogin.getDescriptor());
        anyFieldDescriptors.add(LogUploadMessage.LogUploadLoginAck.getDescriptor());
        anyFieldDescriptors.add(LogUploadMessage.StartNodeLogUpload.getDescriptor());
        anyFieldDescriptors.add(LogUploadMessage.StartNodeLogUploadAck.getDescriptor());
        anyFieldDescriptors.add(LogUploadMessage.ReportLogUploadInfo.getDescriptor());
        /**
         * compute
         */
        anyFieldDescriptors.add(ComputeMessage.InstanceEventReport.getDescriptor());
        anyFieldDescriptors.add(ComputeMessage.InstanceEventReportAck.getDescriptor());
        /**
         * vmagent
         */
        anyFieldDescriptors.add(VMAgent.Login.getDescriptor());
        anyFieldDescriptors.add(VMAgent.LoginAck.getDescriptor());
        anyFieldDescriptors.add(VMAgent.ReportNodeInfo.getDescriptor());
        anyFieldDescriptors.add(VMAgent.ReportNodeInfoAck.getDescriptor());
        anyFieldDescriptors.add(VMAgent.RunScriptAction.getDescriptor());
        anyFieldDescriptors.add(VMAgent.RunScriptActionReport.getDescriptor());
        anyFieldDescriptors.add(VMAgent.DownloadFileAction.getDescriptor());
        anyFieldDescriptors.add(VMAgent.DownloadFileStateReport.getDescriptor());
        anyFieldDescriptors.add(VMAgent.UploadFileAction.getDescriptor());
        anyFieldDescriptors.add(VMAgent.UploadFileStateReport.getDescriptor());
        anyFieldDescriptors.add(VMAgent.UploadFileStop.getDescriptor());
        anyFieldDescriptors.add(VMAgent.DownloadFileStop.getDescriptor());
        anyFieldDescriptors.add(VMAgent.RunScriptStop.getDescriptor());
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder().add(anyFieldDescriptors).build();

        jsonFormatPrinter = JsonFormat.printer().includingDefaultValueFields().usingTypeRegistry(typeRegistry);

    }

    /**
     * @param protoBufMessage
     * @return java.lang.String
     * @author Bob.Yang
     * @description 将ProtoBuf 以JSON 格式输出
     * @date 2021-12-25 18:35
     */
    public static <T extends Message> String toJsonFormat(T protoBufMessage) {
        try {
            return jsonFormatPrinter.print(protoBufMessage);
        } catch (Exception e) {
            log.error("ProtoBuf(Type:{}) to JSONFormat Fail", protoBufMessage.getDescriptorForType().getFullName(), e);
        }

        return "Please Check ProtoFormatter.java anyFieldDescriptors Mapping Config!";
    }

}
