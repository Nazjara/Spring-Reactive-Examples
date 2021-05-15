package com.nazjara.client;

import com.nazjara.domain.BeerDto;
import com.nazjara.domain.BeerPagedList;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BeerClient {
    Mono<BeerDto> getBeerById(UUID id, boolean showInventoryOnHand);
    Mono<BeerPagedList> listBeers(int pageNumber, int pageSize, String beerName, String beerStyle, boolean showInventoryOnHand);
    Mono<ResponseEntity> createBeer(BeerDto beerDto);
    Mono<ResponseEntity> updateBeer(BeerDto beerDto);
    Mono<ResponseEntity> deleteBeer(UUID id);
    Mono<BeerDto> getBeerByUPC(String upc);
}
