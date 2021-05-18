package com.nazjara.controller;

import com.nazjara.bootstrap.BeerLoader;
import com.nazjara.dto.BeerDto;
import com.nazjara.dto.BeerPagedList;
import com.nazjara.services.BeerService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebFluxTest(BeerController.class)
@AutoConfigureWebTestClient
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    BeerService beerService;

    BeerDto validBeer;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder()
                .beerName("Test beer")
                .beerStyle("IPA")
                .upc(BeerLoader.BEER_1_UPC)
                .build();
    }

    @Test
    void getBeerById() {
        var id = UUID.randomUUID();

        given(beerService.getById(eq(id), any())).willReturn(validBeer);

        webTestClient.get()
                .uri("/api/v1/beer/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }

    @Test
    void getBeerByUpc() {
        given(beerService.getByUpc(BeerLoader.BEER_1_UPC)).willReturn(validBeer);

        webTestClient.get()
                .uri("/api/v1/beerUpc/" + BeerLoader.BEER_1_UPC)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));
    }

    @Test
    void getListBeers() {
        var beerList = List.of(validBeer);

        var beerPagedList = new BeerPagedList(beerList, PageRequest.of(1, 1), beerList.size());

        given(beerService.listBeers(any(), any(), any(), any())).willReturn(beerPagedList);

        webTestClient.get()
                .uri("/api/v1/beer/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class);
    }
}
