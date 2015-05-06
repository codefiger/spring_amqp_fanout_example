package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

@Configuration
public class ConsumerConfiguration extends GeneralConfiguration {

    @Bean
    public AmqpAdmin createAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    @DependsOn("createAdmin")
    public Queue createQueue(AmqpAdmin admin) {
        return reuseOrCreateQueue(admin, 1);
    }

    private Queue reuseOrCreateQueue(AmqpAdmin admin, int queueCount) {

        String queueName = QUEUE_NAME + queueCount;
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

    @Bean
    @DependsOn("createAdmin")
    public SimpleMessageListenerContainer listenerContainer(AmqpAdmin admin) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(createQueue(admin));
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new RabbitMessageHandler());
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

}
