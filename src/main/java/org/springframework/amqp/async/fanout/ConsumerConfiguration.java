package org.springframework.amqp.async.fanout;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class ConsumerConfiguration {

    public static final String EXCHANGE_NAME = "events";

    private String queueName;

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
        final RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        FanoutExchange fanoutExchange = new FanoutExchange(EXCHANGE_NAME);
        rabbitAdmin.declareExchange(fanoutExchange);
        queueName = rabbitAdmin.declareQueue().getName();
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, EXCHANGE_NAME, "", null);
        rabbitAdmin.declareBinding(binding);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());

        return template;
    }

    @Bean
    @DependsOn("amqpAdmin")
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(queueName);
        final MessageListenerAdapter messageListenerAdapter = messageListenerAdapter();//new MessageListenerAdapter(new RabbitMessageHandler());
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        return jsonMessageConverter;
    }

    public MessageListenerAdapter messageListenerAdapter() {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new RabbitMessageHandler(), jsonMessageConverter());
        listenerAdapter.setDefaultListenerMethod("handleMessage");
        listenerAdapter.setMessageConverter(jsonMessageConverter());
        return listenerAdapter;
    }

}
