package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ProducerConfiguration {

    private static final String EXCHANGE_NAME = "events";


    private static MessageConverter messageConverter = new SimpleMessageConverter();


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
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
            String messageString = getMessage(new String[] { "test" });
            final Message message1 = messageConverter.toMessage(messageString, null);
            rabbitTemplate.send(EXCHANGE_NAME, "", message1);
            System.out.println("Sent: " + object);
        }
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 1) {
            return "Hello World!";
        }
        return joinStrings(strings, " ");
    }

    private static String joinStrings(String[] strings, String delimiter) {
        int length = strings.length;
        if (length == 0) {
            return "";
        }
        StringBuilder words = new StringBuilder(strings[0]);
        for (int i = 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }

}
