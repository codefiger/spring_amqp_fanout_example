package org.springframework.amqp.async.fanout;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProducerStart {

	public static void main(String[] args) throws Exception {
		new AnnotationConfigApplicationContext(ProducerConfiguration.class);
	}

}
