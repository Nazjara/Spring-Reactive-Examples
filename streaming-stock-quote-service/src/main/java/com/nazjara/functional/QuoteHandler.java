package com.nazjara.functional;

import com.nazjara.model.Quote;
import com.nazjara.service.QuoteGeneratorService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuoteHandler {

    private final QuoteGeneratorService service;

    public Mono<ServerResponse> fetchQuotes(ServerRequest request) {
        var size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.fetchQuoteStream(Duration.ofMillis(100L))
                .take(size), Quote.class);
    }

    public Mono<ServerResponse> streamQuotes(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_NDJSON)
                .body(service.fetchQuoteStream(Duration.ofMillis(100L)), Quote.class);
    }
}
