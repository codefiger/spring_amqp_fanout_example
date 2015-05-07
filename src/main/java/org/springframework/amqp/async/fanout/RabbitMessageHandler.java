package org.springframework.amqp.async.fanout;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

public class RabbitMessageHandler {

	private static MessageConverter messageConverter = new SimpleMessageConverter();

	public void handleMessage(String text) {

		System.out.println("Received: " + text);
	}

}
