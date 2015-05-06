package org.springframework.amqp.async.fanout;

public class RabbitMessageHandler {

	public void handleMessage(String text) {
		System.out.println("Received: " + text);
	}

}
