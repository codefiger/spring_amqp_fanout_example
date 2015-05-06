package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
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
public class ProducerConfiguration extends GeneralConfiguration {

    @Bean
    public Queue createQueue1() {

        String queueName = QUEUE_NAME + 1;
        return new Queue(queueName);
    }

    @Bean
    public Queue createQueue2() {

        String queueName = QUEUE_NAME + 2;
        return new Queue(queueName);
    }

    @Bean
    public Queue createQueue3() {

        String queueName = QUEUE_NAME + 3;
        return new Queue(queueName);
    }

    @Bean
    public AmqpAdmin createAdmin(){
        RabbitAdmin admin = new RabbitAdmin(connectionFactory());
        admin.declareQueue(createQueue1());
        admin.declareQueue(createQueue2());
        admin.declareQueue(createQueue3());
        return admin;
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

}
