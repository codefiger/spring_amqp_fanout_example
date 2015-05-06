package org.springframework.amqp.async.fanout;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConsumerStart2 {

	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(ConsumerConfiguration2.class);
	}

}
