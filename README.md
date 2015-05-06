Spring AMQP Fanout Example with RabbitMQ - Each Consumer will get the same message
==================================================================================
This is not JMS. This is RabbitMQ. It differs a bit from what you will know from JMS.

To deliver a message to each consumer, we need a publish/subscribe pattern.
For RabbitMQ, this is a fanout with one dedicated queue per consumer.
The Exchange is a default, unnamed. Thus, the Consumer just has to declare its own queue with @Bean in order to bind and receive a message.

Run ConsumerStart twice. Run ProducerStart. Both Consumers will get the same message!