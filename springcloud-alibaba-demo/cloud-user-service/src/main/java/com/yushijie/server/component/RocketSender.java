package com.yushijie.server.component;

//@Component
public class RocketSender {

//    public void send() {
//
//        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
//        try {
//            // 创建生产者实例，并设置生产者组名
//            // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
//            producer.setNamesrvAddr("192.168.16.110:9876");
//            producer.start();
//            // 创建消息实例，指定 topic、Tag和消息体
//            Message msg = new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes());
//            // 发送消息并获取发送结果
//            SendResult sendResult = producer.send(msg);
//            System.out.println("Message sent: " + new String(msg.getBody()));
//            System.out.println("Send result: " + sendResult);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Message sending failed.");
//        } finally {
//            // 关闭生产者
//            producer.shutdown();
//        }
//    }

}
