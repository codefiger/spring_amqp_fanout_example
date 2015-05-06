package org.springframework.amqp.async.fanout;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class GeneralConfiguration {

    public static final String EXCHANGE_NAME = "fanout";
    public static final String QUEUE_NAME = "event.queue.name";
    public static final String ROUTING_KEY = "event.key.name";


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(ROUTING_KEY);
        template.setExchange(EXCHANGE_NAME);
        return template;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setHost("localhost");
        connectionFactory.setVirtualHost("vhost1");
        connectionFactory.setUsername("testuser1");
        connectionFactory.setPassword("pass1");
        return connectionFactory;
    }

}
