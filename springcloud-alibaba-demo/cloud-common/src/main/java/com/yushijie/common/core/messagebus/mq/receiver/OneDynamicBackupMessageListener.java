package com.yushijie.common.core.messagebus.mq.receiver;

/**
 * @description
 * @author yushijie
 * @date 2024/9/3 16:34
 * @version 1.0
 */

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.protobuf.Any;
import com.rabbitmq.client.Channel;

import cn.keptdata.one2data.api.backup.BackupApi;
import cn.keptdata.one2data.core.log.Log;
import cn.keptdata.one2data.core.log.LogFactory;
import cn.keptdata.one2data.core.messagebus.core.AsyncMessageCoordinator;
import cn.keptdata.one2data.core.messagebus.core.MessageBusHelper;
import cn.keptdata.one2data.core.messagebus.mq.OneMessageConverter;
import cn.keptdata.one2data.header.message.protobuf.client.BackupCommand;
import cn.keptdata.one2data.header.message.protobuf.client.ClientMessage;
import cn.keptdata.one2data.header.message.protobuf.common.Common;

@Component
public class OneDynamicBackupMessageListener implements ChannelAwareMessageListener {

    private static final Log log = LogFactory.getLog();

    @Autowired
    private OneMessageConverter oneMessageConverter;

    @Lazy
    @Autowired
    private BackupApi backupApi;

    @Lazy
    @Autowired
    private AsyncMessageCoordinator asyncMessageCoordinator;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {

        try {

            Object fromMessage = oneMessageConverter.fromMessage(message);

            // if (fromMessage instanceof Common.Message) {
            Common.Message protoFromMessage = (Common.Message)fromMessage;

            Any body = protoFromMessage.getBody();

            Common.Header header = protoFromMessage.getHeader();

            if (body.is(BackupCommand.ActionAck.class)) {

                BackupCommand.ActionAck actionAck = body.unpack(BackupCommand.ActionAck.class);

                BackupCommand.ActionType actionType = actionAck.getActionType();

                String reportTaskUuid = actionAck.getBackupTaskUuid();

                if (actionType.equals(BackupCommand.ActionType.AT_SYNC) && actionAck.getBaseAck().getIsSuccess()) {

                    if (backupApi.isClusterBackupTask(reportTaskUuid)) {

                        if (backupApi.isFirstSharedActionAckTagExist(reportTaskUuid)) {

                            backupApi.delFirstSharedActionAckTag(reportTaskUuid);

                            backupApi.fireEngineStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_SYNC);

                            backupApi.fireEngineWorkStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_SYNC);

                        }

                    } else {

                        backupApi.fireEngineStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_SYNC);

                        backupApi.fireEngineWorkStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_SYNC);

                    }

                }

                if (actionType.equals(BackupCommand.ActionType.AT_FULL_MIRRORING)
                    && actionAck.getBaseAck().getIsSuccess()) {

                    backupApi.fireEngineStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_FULL_MIRRORING);

                    backupApi.fireEngineWorkStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_FULL_MIRRORING);

                }

                if (actionType.equals(BackupCommand.ActionType.AT_CHECK) && actionAck.getBaseAck().getIsSuccess()) {

                    backupApi.fireEngineStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_CHECK);

                    backupApi.fireEngineWorkStateTransition(reportTaskUuid, BackupCommand.ActionType.AT_CHECK);

                }

                if (actionType.equals(BackupCommand.ActionType.AT_PAUSED)) {
                    // 异常状态下支持停止任务 删除虚状态
                    backupApi.deleteVirtState(reportTaskUuid);

                }

                // 重置进度
                backupApi.resetBackupProgress(reportTaskUuid);

                // 当前消息是否需要异步转同步
                if (needSyncAck(protoFromMessage)) {

                    asyncMessageCoordinator.broadcastCommonMessage(protoFromMessage);

                } else {

                    // 无需异步处理

                }

            }

            if (body.is(ClientMessage.BackupWorkStateReport.class)) {

                ClientMessage.BackupWorkStateReport backupWorkStateReport =
                    body.unpack(ClientMessage.BackupWorkStateReport.class);

                backupApi.handleBackupWorkStateReportMessage(backupWorkStateReport, header);

            }
            // }
            //
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Throwable ex) {

            log.error("receive BackupTaskOpReply Message Error", ex);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

        }

    }

    public boolean needSyncAck(Common.Message ackMessage) {
        return MessageBusHelper.needSyncAck(ackMessage);
    }

}
