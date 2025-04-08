package com.zqq.common.rabbitmq;

import com.zqq.common.core.constants.RabbitMQConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * rabbitmq配置类
 */
@Configuration
public class RabbitConfig {

    /**
     * 注册了一个 RabbitMQ 的队列，名字是 RabbitMQConstants.OJ_WORK_QUEUE，并且设置为 持久化（true 表示重启后队列还存在）。
     * @return
     */
    @Bean
    public Queue workQueue(){
        return new Queue(RabbitMQConstants.OJ_WORK_QUEUE,true);
    }

    /**
     * 	注册了一个 消息转换器，告诉 RabbitMQ 和 Spring：
     * 发送/接收消息时，使用 Jackson（即 JSON 格式）来进行序列化/反序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
