package com.nazjara.functional;

import com.nazjara.controller.NotFoundException;
import com.nazjara.dto.BeerDto;
import com.nazjara.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerHandlerV2 {
    private final BeerService beerService;
    private final Validator validator;

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        var beerId = Integer.valueOf(request.pathVariable("beerId"));
        var showInventory = Boolean.valueOf(request.queryParam("showInventory").orElse("false"));

        return beerService.getById(beerId, showInventory)
                .flatMap(beerDto -> ServerResponse.ok().bodyValue(beerDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getBeerByUpc(ServerRequest request) {
        return beerService.getByUpc(request.pathVariable("upc"))
                .flatMap(beerDto -> ServerResponse.ok().bodyValue(beerDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveBeer(ServerRequest request) {
        return request.bodyToMono(BeerDto.class)
                .doOnNext(this::validate)
                .flatMap(beerService::saveNewBeer)
                .flatMap(savedBeerDto -> ServerResponse.ok()
                        .header("location", "/api/v2/beer" + savedBeerDto.getId())
                        .build());
    }

    public Mono<ServerResponse> updateBeer(ServerRequest request) {
        return request.bodyToMono(BeerDto.class)
                .doOnNext(this::validate)
                .flatMap(beerDto -> beerService.updateBeer(Integer.valueOf(request.pathVariable("beerId")), beerDto))
                .flatMap(savedBeerDto -> savedBeerDto.getId() != null ? ServerResponse.noContent().build() :
                        ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteBeer(ServerRequest request) {
        return beerService.deleteBeerByIdReactive(Integer.valueOf(request.pathVariable("beerId")))
                .flatMap(response -> ServerResponse.noContent().build())
                .onErrorResume(ex -> ex instanceof NotFoundException, ex -> ServerResponse.notFound().build());
    }

    private void validate(BeerDto beerDto) {
        var errors = new BeanPropertyBindingResult(beerDto, "beerDto");
        validator.validate(beerDto, errors);

        if(errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }
}
