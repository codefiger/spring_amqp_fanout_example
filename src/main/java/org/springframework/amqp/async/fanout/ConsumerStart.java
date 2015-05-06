package org.springframework.amqp.async.fanout;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConsumerStart {

	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
	}

}
