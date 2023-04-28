package com.yushijie.rpcserver.config;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/4/20 15:23
 */
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 江南一点雨
 * @微信公众号 江南一点雨
 * @网站 http://www.itboyhub.com
 * @国际站 http://www.javaboy.org
 * @微信 a_java_boy
 * @GitHub https://github.com/lenve
 * @Gitee https://gitee.com/lenve
 */
@Configuration
public class RabbitConfig {

    public static final String FAKE_QUEUE = "amq.rabbitmq.reply-to";
    public static final String RPC_QUEUE = "rpc.queue";
    public static final String PRC_EXCHANGE = "";

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

//    @Autowired
//    ConnectionFactory connectionFactory;

    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/one2data");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置reply_to（返回队列，只能在这设置）
//        rabbitTemplate.setReplyAddress(FAKE_QUEUE);
//        rabbitTemplate.setReplyTimeout(5000);
        return rabbitTemplate;
    }
    //返回队列监听器（必须有）
//    @Bean(name="replyMessageListenerContainer")
//    public SimpleMessageListenerContainer createReplyListenerContainer() {
//        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
//        listenerContainer.setConnectionFactory(connectionFactory);
//        listenerContainer.setQueueNames(FAKE_QUEUE);
//        listenerContainer.setMessageListener(rabbitTemplate());
//        return listenerContainer;
//    }
    /*//创建队列
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1);
    }
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2);
    }

    //创建交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    //交换机与队列进行绑定
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_QUEUE1);
    }
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_QUEUE2);
    }*/
}
