package com.nazjara.config;

import com.rabbitmq.client.Connection;
import java.io.IOException;
import javax.annotation.PreDestroy;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "quotes";

    @Autowired
    public Mono<Connection> connection;

    @Bean
    Mono<Connection> connection(CachingConnectionFactory connectionFactory) {
        return Mono.fromCallable(() -> connectionFactory.getRabbitConnectionFactory().newConnection());
    }

    @Bean
    Sender sender() {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connection));
    }

    @Bean
    Receiver receiver() {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connection));
    }

    @PreDestroy
    public void close() throws IOException {
        connection.block().close();
    }
}
