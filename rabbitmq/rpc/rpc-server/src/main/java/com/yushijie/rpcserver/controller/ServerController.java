package com.yushijie.rpcserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushijie.rpcserver.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/4/20 15:18
 */
@RestController
public class ServerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.RPC_QUEUE)
    public void receive(Message message) throws Exception {
        byte[] body = message.getBody();
        System.out.println(new String(body));
        MessageProperties messageProperties = message.getMessageProperties();
        String correlationId = messageProperties.getCorrelationId();
        System.out.println("correlationId = " + correlationId);
        String replyTo = messageProperties.getReplyTo();
        System.out.println("replyTo = " + replyTo);

        Thread.sleep(15000L);
        RabbitAdmin admin = new RabbitAdmin(rabbitTemplate);
        Properties queueProperties = admin.getQueueProperties(replyTo);
        System.out.println("queueProperties = " + queueProperties);

        MessageProperties replyMessageProperties = message.getMessageProperties();
        replyMessageProperties.setCorrelationId(correlationId);
        Message replyMessage = new Message("reply".getBytes(StandardCharsets.UTF_8), replyMessageProperties);
        rabbitTemplate.send("", replyTo, replyMessage);

        Queue tempQueue = new Queue(UUID.randomUUID().toString(), false);
        admin.declareQueue(tempQueue);
    }

}
