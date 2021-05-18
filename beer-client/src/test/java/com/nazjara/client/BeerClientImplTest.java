package com.nazjara.client;

import com.nazjara.configuration.WebConfiguration;
import com.nazjara.domain.BeerDto;
import com.nazjara.domain.BeerStyleEnum;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerClientImplTest {

    BeerClient beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebConfiguration().webClient());
    }

    @Test
    void getBeerById() {
        var beerPagedListMono = beerClient.listBeers(1, 10, null, null, false);

        var beerPagedList = beerPagedListMono.block();

        var id = beerPagedList.getContent().get(0).getId();

        var beerDto = beerClient.getBeerById(id, false).block();

        assertThat(beerDto.getId()).isEqualTo(id);
    }

    @Test
    void getBeerByIdFunctional() throws InterruptedException {
        var beerName = new AtomicReference<>();
        var countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(1, 10, null, null, false)
                .map(beerList -> beerList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(beerMono -> beerMono)
                .subscribe(beerDto -> {
                    beerName.set(beerDto.getBeerName());
                    countDownLatch.countDown();
                });

        countDownLatch.await();

        assertThat(beerName.get()).isEqualTo("Mango Bobs");
    }

    @Test
    void listBeers() {
        var beerPagedListMono = beerClient.listBeers(1, 10, null, null, false);

        var beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(10);
    }

    @Test
    void createBeer() {
        var beerDto = BeerDto.builder()
                .beerName("Beer 1")
                .beerStyle(BeerStyleEnum.IPA)
                .upc("12345667")
                .price(new BigDecimal("10.99"))
                .build();

        var response = beerClient.createBeer(beerDto).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {
        var beerPagedListMono = beerClient.listBeers(1, 10, null, null, false);

        var beerPagedList = beerPagedListMono.block();

        var beerDto = beerPagedList.getContent().get(0);

        var beerDtoToUpdate = BeerDto.builder()
                .beerName("Beer 1")
                .beerStyle(BeerStyleEnum.IPA)
                .upc("12345667")
                .price(new BigDecimal("10.99"))
                .build();

        var response = beerClient.updateBeer(beerDto.getId(), beerDtoToUpdate).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteBeer() {
        var beerPagedListMono = beerClient.listBeers(1, 10, null, null, false);

        var beerPagedList = beerPagedListMono.block();

        var beerDto = beerPagedList.getContent().get(0);

        var response = beerClient.deleteBeer(beerDto.getId()).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteBeerNotFound() {
        assertThrows(WebClientResponseException.class, () -> beerClient.deleteBeer(UUID.randomUUID()).block());
    }

    @Test
    void deleteBeerHandleException() {
        var response = beerClient.deleteBeer(UUID.randomUUID()).onErrorResume(throwable -> {
            if (throwable instanceof WebClientResponseException)
            {
                var exception = (WebClientResponseException) throwable;
                return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
            } else {
                throw new RuntimeException(throwable);
            }
        }).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getBeerByUPC() {
        var beerPagedListMono = beerClient.listBeers(1, 10, null, null, false);

        var beerPagedList = beerPagedListMono.block();

        var upc = beerPagedList.getContent().get(0).getUpc();

        var beerDto = beerClient.getBeerByUPC(upc).block();

        assertThat(beerDto.getUpc()).isEqualTo(upc);
    }
}
