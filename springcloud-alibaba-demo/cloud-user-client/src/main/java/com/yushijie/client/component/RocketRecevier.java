package com.yushijie.client.component;

import jakarta.annotation.PostConstruct;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class RocketRecevier {

//    @PostConstruct
//    public void receive() {
//        receiveMsg();
//    }

    public void receiveMsg() {
//        try {
//            // 创建消费者实例，并设置消费者组名
//            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
//            // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
//            consumer.setNamesrvAddr("192.168.16.110:9876");
//            // 订阅指定的主题和标签（* 表示所有标签）
//            consumer.subscribe("TestTopic", "*");
//
//            // 注册消息监听器
//            consumer.registerMessageListener(new MessageListenerConcurrently() {
//                @Override
//                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                    for (MessageExt msg : msgs) {
//                        System.out.println("Received message: " + new String(msg.getBody()));
//                    }
//                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                }
//            });
//
//            // 启动消费者
//            consumer.start();
//            System.out.println("Consumer started.");
//        } catch (MQClientException e) {
//            throw new RuntimeException(e);
//        }
    }

}
