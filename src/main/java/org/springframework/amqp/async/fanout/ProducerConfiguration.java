package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ProducerConfiguration {


    public static final String QUEUE_NAME = GeneralConfiguration.QUEUE_NAME + 100;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //template.setExchange(ExchangeTypes.FANOUT);
        template.setRoutingKey(QUEUE_NAME);
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

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public ScheduledProducer scheduledProducer() {
        return new ScheduledProducer();
    }

    @Bean
    public BeanPostProcessor postProcessor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }


    static class ScheduledProducer {

        @Autowired
        private volatile RabbitTemplate rabbitTemplate;

        private final AtomicInteger counter = new AtomicInteger();

        @Scheduled(fixedRate = 3000)
        public void sendMessage() {
            final int i = counter.incrementAndGet();
            final String object = "Hello New World " + i;
            rabbitTemplate.convertAndSend(object);
            System.out.println("Received: " + object);
        }
    }

    private void deleteExistingQueues(RabbitAdmin admin) {
        admin.deleteQueue(GeneralConfiguration.QUEUE_NAME+1);
        admin.deleteQueue(GeneralConfiguration.QUEUE_NAME+2);
        admin.deleteQueue(GeneralConfiguration.QUEUE_NAME+3);
    }
}
