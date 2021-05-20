package com.nazjara.mapper;

import com.nazjara.domain.Beer;
import com.nazjara.dto.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface BeerMapper {
    @Mapping(target = "quantityOnHand", ignore = true)
    BeerDto beerToBeerDto(Beer beer);
    BeerDto beerToBeerDtoWithInventory(Beer beer);
    Beer beerDtoToBeer(BeerDto dto);
}
