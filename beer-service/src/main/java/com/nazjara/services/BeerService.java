package com.nazjara.services;

import com.nazjara.dto.BeerDto;
import com.nazjara.dto.BeerPagedList;
import com.nazjara.dto.BeerStyleEnum;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

public interface BeerService {
    Mono<BeerPagedList> listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand);
    Mono<BeerDto> getById(Integer beerId, Boolean showInventoryOnHand);
    Mono<BeerDto> saveNewBeer(BeerDto beerDto);
    Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto);
    Mono<BeerDto> getByUpc(String upc);
    void deleteBeerById(Integer beerId);
}
