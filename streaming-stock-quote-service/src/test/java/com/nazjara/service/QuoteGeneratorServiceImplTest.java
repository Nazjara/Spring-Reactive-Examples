package com.nazjara.service;

import com.nazjara.model.Quote;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteGeneratorServiceImplTest {

    QuoteGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new QuoteGeneratorServiceImpl();
    }

    @Test
    void fetchQuoteStream() throws InterruptedException {
        var quoteFlux = service.fetchQuoteStream(Duration.ofMillis(100L));

        Consumer<Quote> quoteConsumer = System.out::println;

        Consumer<Throwable> errorConsumer = ex -> System.out.println(ex.getMessage());

        var countDownLatch = new CountDownLatch(1);

        Runnable done = countDownLatch::countDown;

        quoteFlux.take(30)
                .subscribe(quoteConsumer, errorConsumer, done);

        countDownLatch.await();
    }
}