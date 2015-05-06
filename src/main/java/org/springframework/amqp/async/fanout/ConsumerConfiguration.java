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

//        String queueName = QUEUE_NAME + 1;
//        Properties queueProperties;
//        final int possibleMlBackendAPICores = 10;
//        for (int i = 1; i <= possibleMlBackendAPICores; i++) {
//            queueName = QUEUE_NAME + i;
//            queueProperties = admin.getQueueProperties(queueName);
//            if ( queueProperties != null && noConsumers(queueProperties) ) {
//                return new Queue(queueName);
//            } else {
//                queueName = QUEUE_NAME + (i + 1);
//                Queue queue = new Queue(queueName);
//                admin.declareQueue(queue);
//                return queue;
//            }
//        }
//        throw new RuntimeException(new RabbitQueueInitializationException("Queue could not be initialized for " + queueName));
        return reuseOrCreateQueue(admin, 1);
    }

    private Queue reuseOrCreateQueue(AmqpAdmin admin, int queueCount) {

        String queueName = QUEUE_NAME + queueCount;
        Properties queueProperties;
        queueProperties = admin.getQueueProperties(queueName);

        if (queueProperties == null) {

            return registerQueue(admin, queueName);

        } else if (queueProperties != null && noConsumers(queueProperties)) {

            return registerQueue(admin, queueName);

        } else {

            return reuseOrCreateQueue(admin, queueCount + 1);
        }
    }

    private Queue reuseExistingQueue(String queueName) {
        return new Queue(queueName);
    }

    private Queue registerQueue(AmqpAdmin admin, String queueName) {
        final Queue queue = new Queue(queueName);
        //admin.declareQueue(queue);
        return queue;
    }


    private boolean noConsumers(Properties queueProperties) {
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

    public class RabbitQueueInitializationException extends Exception {
        public RabbitQueueInitializationException(String msg) {
            super(msg);
        }
    }

}
