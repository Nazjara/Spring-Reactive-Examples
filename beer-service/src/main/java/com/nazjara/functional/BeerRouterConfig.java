package com.nazjara.functional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BeerRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> beerRoutesV2(BeerHandlerV2 handler) {
        return route()
                .GET("/api/v2/beer/{beerId}", accept(MediaType.APPLICATION_JSON), handler::getBeerById)
                .GET("/api/v2/beerUpc/{upc}", accept(MediaType.APPLICATION_JSON), handler::getBeerByUpc)
                .POST("/api/v2/beer", accept(MediaType.APPLICATION_JSON), handler::saveBeer)
                .PUT("/api/v2/beer/{beerId}", accept(MediaType.APPLICATION_JSON), handler::updateBeer)
                .DELETE("/api/v2/beer/{beerId}", accept(MediaType.APPLICATION_JSON), handler::deleteBeer)
                .build();
    }
}
