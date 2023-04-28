package com.yushijie.rpcclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushijie.rpcclient.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/4/20 15:17
 */
@RestController
public class ClientController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void send() throws Exception {
        MessageProperties properties = new MessageProperties();
        String uuid = UUID.randomUUID().toString();
        System.out.println("uuid = " + uuid);
        properties.setCorrelationId(uuid);
        properties.setReplyTo(RabbitConfig.FAKE_QUEUE);
        Message message = new Message("yushijie".getBytes(StandardCharsets.UTF_8), properties);

        Message receive = rabbitTemplate.sendAndReceive(RabbitConfig.RPC_EXCHANGE, RabbitConfig.RPC_ROUTING, message);
        MessageProperties messageProperties = receive.getMessageProperties();
        String correlationId = messageProperties.getCorrelationId();
        System.out.println("correlationId = " + correlationId);
        byte[] body = receive.getBody();
        System.out.println(new String(body));
    }

}
