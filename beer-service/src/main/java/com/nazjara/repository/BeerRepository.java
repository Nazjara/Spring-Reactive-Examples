package com.nazjara.repository;

import com.nazjara.domain.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
//    Flux<Page<Beer>> findAllByBeerName(String beerName, Pageable pageable);
//    Flux<Page<Beer>> findAllByBeerStyle(BeerStyleEnum beerStyle, Pageable pageable);
//    Flux<Page<Beer>> findAllByBeerNameAndBeerStyle(String beerName, BeerStyleEnum beerStyle, Pageable pageable);
//    Flux<Page<Beer>> findBeerBy(Pageable pageable);
    Mono<Beer> findByUpc(String upc);
}
