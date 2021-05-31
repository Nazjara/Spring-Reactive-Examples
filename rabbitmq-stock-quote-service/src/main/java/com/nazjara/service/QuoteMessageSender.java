package com.nazjara.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nazjara.model.Quote;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;
import static com.nazjara.config.RabbitMQConfig.QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteMessageSender {
    private final ObjectMapper objectMapper;
    private final Sender sender;

    @SneakyThrows
    public Mono<Void> sendQuoteMessage(Quote quote) {
        var jsonBytes = objectMapper.writeValueAsBytes(quote);

        var confirmations = sender.sendWithPublishConfirms(
                Flux.just(new OutboundMessage("", QUEUE, jsonBytes)));

        sender.declareQueue(QueueSpecification.queue(QUEUE))
                .thenMany(confirmations)
                .doOnError(error -> log.error("Send failed", error))
                .subscribe(message -> {
                    if (message.isAck()) {
                        log.info("Message sent successfully {}", new String(message.getOutboundMessage().getBody()));
                    }
                });

        return Mono.empty();
    }
}
