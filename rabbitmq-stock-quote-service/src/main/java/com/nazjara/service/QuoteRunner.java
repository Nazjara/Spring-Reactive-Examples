package com.nazjara.service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.Receiver;
import static com.nazjara.config.RabbitMQConfig.QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {

    private final QuoteGeneratorService quoteGeneratorService;
    private final QuoteMessageSender messageSender;
    private final Receiver receiver;

    @Override
    public void run(String... args) {
        quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(1000L))
                .take(50)
                .log("Got Quote")
                .flatMap(messageSender::sendQuoteMessage)
                .subscribe(result -> log.info("Sent message to RabbitMQ"),
                        throwable -> log.info("Error occurred: " + throwable.getMessage()),
                        () -> log.info("Done"));

        var receivedCount = new AtomicInteger();

        receiver.consumeAutoAck(QUEUE)
                .subscribe(message -> log.info("Received message # {} - {}", receivedCount.incrementAndGet(),
                    new String(message.getBody())),
                        throwable -> log.info("Error occurred: " + throwable.getMessage()),
                        () -> log.info("Done"));
    }
}
