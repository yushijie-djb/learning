package com.yushijie.backup.service.impl;

import com.yushijie.backup.service.BackupTaskService;
import com.yushijie.common.backup.BackupTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName BackupTaskServiceImpl
 * @Description
 * @Author yu155
 * @Date 2025/9/26 16:25
 * @Version 1.0.0
 */
@Service
public class BackupTaskServiceImpl implements BackupTaskService {

    @Autowired
    private BackupTaskService self;

    @Override
    public void batchAsyncStartBackupTasks(List<String> backupTaskUuids) {
        backupTaskUuids.forEach(
                uuid -> self.asyncStartBackupTask(uuid)
        );
    }

    @Override
    public void asyncStartBackupTask(String backupTaskUuid) {

    }

    private void asyncOperateBackupTask(String backupTaskUuid,
                                                     Class<? extends BaseJobHandler> jobHandler) {
        // 备份任务信息
        BackupTask backupTask = self.getBackupTaskByUuid(backupTaskUuid);

        // 备份任务关联的客户端信息
        Device relatedDevice = deviceService.findByUuid(backupTask.getDeviceUuid());

        // 构造创建备份任务元数据结构信息
        BackupTaskOperateDTO backupTaskOperate = new BackupTaskOperateDTO(backupTaskUuid);
        AsyncJobInfo<BackupTaskOperateDTO> asyncJobInfo =
                AsyncJobInfo.<BackupTaskOperateDTO>builder().jobHandler(jobHandler).request(backupTaskOperate).build();

        // 附加任务关联的资源信息
        JobHelper.attachResources(asyncJobInfo, backupTask, relatedDevice);

        return jobService.submitAsyncJob(asyncJobInfo);
    }
}
