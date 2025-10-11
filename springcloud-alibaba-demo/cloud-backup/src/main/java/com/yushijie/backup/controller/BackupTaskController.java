package com.yushijie.backup.controller;

import com.yushijie.backup.service.BackupTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName BackupTaskController
 * @Description
 * @Author yu155
 * @Date 2025/9/26 16:16
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/1.0/backup/backup-tasks")
public class BackupTaskController {

    @Autowired
    private BackupTaskService backupTaskService;

    @PostMapping("/batch/actions/start")
    public void batchAsyncStartBackupTask(@RequestBody List<String> backupTaskUuids) {
        backupTaskService.batchAsyncStartBackupTasks(backupTaskUuids);
    }

}
