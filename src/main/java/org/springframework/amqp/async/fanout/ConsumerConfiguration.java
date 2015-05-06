package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

@Configuration
public class ConsumerConfiguration {

    public static final String QUEUE_NAME = GeneralConfiguration.QUEUE_NAME + 100;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setHost("localhost");
        connectionFactory.setVirtualHost("vhost1");
        connectionFactory.setUsername("testuser1");
        connectionFactory.setPassword("pass1");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey(QUEUE_NAME);
        //Where we will synchronously receive messages from
        template.setQueue(QUEUE_NAME);
        return template;
    }

    @Bean
    // Every queue is bound to the default direct exchange
    public Queue helloWorldQueue() {
        return new Queue(GeneralConfiguration.QUEUE_NAME+100);
    }

    @Bean
    @DependsOn("amqpAdmin")
    public SimpleMessageListenerContainer listenerContainer(AmqpAdmin admin) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        //TODO #reuseOrCreateQueue()
        container.setQueueNames(QUEUE_NAME);
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new RabbitMessageHandler());
        container.setMessageListener(messageListenerAdapter);
        return container;
    }



    private Queue reuseOrCreateQueue(AmqpAdmin admin, int queueCount) {

        String queueName = GeneralConfiguration.QUEUE_NAME + queueCount;
        Properties queueProperties;
        queueProperties = admin.getQueueProperties(queueName);

        if (queueDoesNotExist(queueProperties) ||
                queueExists(queueProperties) && hasNoConsumers(queueProperties)) {

            return useQueue(queueName);

        } else {

            return reuseOrCreateQueue(admin, queueCount + 1);
        }
    }

    private boolean queueDoesNotExist(Properties queueProperties) {
        return queueProperties == null;
    }

    private boolean queueExists(Properties queueProperties) {
        return queueProperties != null;
    }

    private Queue useQueue(String queueName) {
        return new Queue(queueName);
    }

    private boolean hasNoConsumers(Properties queueProperties) {
        return ((Integer) queueProperties.get("QUEUE_CONSUMER_COUNT")) == 0;
    }

}
