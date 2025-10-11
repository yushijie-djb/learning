package com.yushijie.backup.service;

import java.util.List;

public interface BackupTaskService {
    void batchAsyncStartBackupTasks(List<String> backupTaskUuids);
    void asyncStartBackupTask(String backupTaskUuid);
}
