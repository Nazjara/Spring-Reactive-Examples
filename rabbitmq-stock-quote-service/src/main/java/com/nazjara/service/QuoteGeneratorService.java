package com.nazjara.service;

import com.nazjara.model.Quote;
import java.time.Duration;
import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {
    Flux<Quote> fetchQuoteStream(Duration period);
}
