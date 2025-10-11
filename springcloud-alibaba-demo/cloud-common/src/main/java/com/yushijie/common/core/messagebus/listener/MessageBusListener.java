package com.yushijie.common.core.messagebus.listener;

import java.util.List;
import java.util.Objects;

import org.springframework.amqp.core.QueueInformation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cn.keptdata.one2data.api.client.ClientApi;
import cn.keptdata.one2data.core.messagebus.MessageBus;
import cn.keptdata.one2data.core.spring.SpringContextUtil;

/**
 * @description
 * @author yushijie
 * @date 2024/9/11 14:04
 * @version 1.0
 */
public class MessageBusListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationReadyEvent) {

            ClientApi clientApi = SpringContextUtil.getBean(ClientApi.class);

            MessageBus messageBus = SpringContextUtil.getBean(MessageBus.class);

            // rebingding client2web listener
            List<String> allDeviceUuidKey = clientApi.getAllDeviceUuidKey();

            for (String uuidKey : allDeviceUuidKey) {

                String formatQueueName = String.format("one2data.backup.client2web.%s", uuidKey);

                QueueInformation queueInfo = messageBus.getQueueInfo(formatQueueName);

                if (Objects.isNull(queueInfo)) {

                    messageBus.addQueue(formatQueueName);

                    messageBus.bindingQueue(formatQueueName, "one2data.backup.client2web", formatQueueName);

                }

                messageBus.addBackupQueueListener(formatQueueName);

            }

            // delete one2data.backup.client2web.actionAck
            // delete one2data.backup.client2web.backupWorkStateReport
            // todo 版本迭代后删除旧队列
//            messageBus.deleteQueue("one2data.backup.client2web.actionAck");
//
//            messageBus.deleteQueue("one2data.backup.client2web.backupWorkStateReport");

        }

    }

}
