package com.project.ratelimit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "audit.log.queue";
    public static final String EXCHANGE_NAME = "audit.log.exchange";
    public static final String ROUTING_KEY = "audit.log.key";

    @Bean
    public Queue auditLogQueue() {
        // durable = true means queue survives RabbitMQ broker restarts
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange auditLogExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding auditLogBinding(Queue auditLogQueue, DirectExchange auditLogExchange) {
        return BindingBuilder.bind(auditLogQueue).to(auditLogExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
