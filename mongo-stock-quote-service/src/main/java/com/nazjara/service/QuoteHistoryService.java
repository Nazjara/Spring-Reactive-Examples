package com.nazjara.service;

import com.nazjara.domain.QuoteHistory;
import com.nazjara.model.Quote;
import reactor.core.publisher.Mono;

public interface QuoteHistoryService {

    Mono<QuoteHistory> saveQuoteToMongo(Quote quote);
}
