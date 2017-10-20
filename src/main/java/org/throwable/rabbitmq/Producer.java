package org.throwable.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/20 18:33
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //dlx交互器，实质上是一个direct类型的交换器
        channel.exchangeDeclare("dlx-exchange", "direct");

        //dlx转发目标队列
        channel.queueDeclare("dlx-consume-queue", true, false, false, null);
        //dlx交换器绑定到转发队列
        channel.queueBind("dlx-consume-queue", "dlx-exchange", "dlx-exchange-routing-key");

        Map<String, Object> queueArgs = new HashMap<>();
        queueArgs.put("x-dead-letter-exchange", "dlx-exchange");
        queueArgs.put("x-dead-letter-routing-key", "dlx-exchange-routing-key");
        channel.exchangeDeclare("queue-ttl-dlx", "direct");
        channel.queueBind("queue-ttl-dlx", "queue-ttl-dlx", "queue-ttl-dlx");
        channel.queueDeclare("queue-ttl-dlx", true, false, false, queueArgs);
        byte i = 10;
        do {
            channel.basicPublish("queue-ttl-dlx", "queue-ttl-dlx", new AMQP.BasicProperties.Builder().expiration(String.valueOf(i * 1000)).build(),
                    new byte[]{i});
            i--;
        } while (i >= 0);

        channel.close();
        connection.close();
    }
}
