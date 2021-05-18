package com.nazjara.services;

import com.nazjara.dto.BeerDto;
import com.nazjara.dto.BeerPagedList;
import com.nazjara.dto.BeerStyleEnum;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand);
    BeerDto getById(UUID beerId, Boolean showInventoryOnHand);
    BeerDto saveNewBeer(BeerDto beerDto);
    BeerDto updateBeer(UUID beerId, BeerDto beerDto);
    BeerDto getByUpc(String upc);
    void deleteBeerById(UUID beerId);
}
