package org.throwable.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/20 18:33
 */
public class Consumer {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicConsume("dlx-consume-queue", true, "consumer-xxxxx", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                long deliveryTag = envelope.getDeliveryTag();

                //do some work async
                System.out.println(String.format("currentTime [%s], deliveryTag --> %s, value --> %s", LocalDateTime.now().format(FORMATTER), deliveryTag, body[0]));
            }
        });
    }
}
