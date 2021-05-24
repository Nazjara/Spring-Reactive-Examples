package com.nazjara.controller;

import com.nazjara.bootstrap.BeerLoader;
import com.nazjara.dto.BeerDto;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientV2IntegrationTest {
    public static final String BASE_URL = "http://localhost:8080";

    WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                .build();
    }

    @Test
    void testDeleteBeer() {
        var beerId = 3;
        var countDownLatch = new CountDownLatch(1);

        webClient.delete().uri("/api/v2/beer/" + beerId )
                .retrieve().toBodilessEntity()
                .flatMap(responseEntity -> {
                    countDownLatch.countDown();

                    return webClient.get().uri("/api/v2/beer/" + beerId)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve().bodyToMono(BeerDto.class);
                }) .subscribe(savedDto -> {

        }, throwable -> countDownLatch.countDown());
    }

    @Test
    void testDeleteBeerNotFound() {
        var beerId = 4;

        webClient.delete().uri("/api/v2/beer/" + beerId )
                .retrieve().toBodilessEntity().block();

        assertThrows(WebClientResponseException.NotFound.class, () -> {
            webClient.delete().uri("/api/v2/beer/" + beerId )
                    .retrieve().toBodilessEntity().block();
        });
    }

    @Test
    void testUpdateBeerNotFound() throws InterruptedException {
        var newBeerName = "JTs Beer";
        var beerId = 999;
        var countDownLatch = new CountDownLatch(1);

        webClient.put().uri("/api/v2/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                .fromValue(BeerDto.builder()
                        .beerName(newBeerName)
                        .upc("1233455")
                        .beerStyle("PALE_ALE")
                        .price(new BigDecimal("8.99"))
                        .build()))
                .retrieve().toBodilessEntity()
                .subscribe(responseEntity -> {
                    assertThat(responseEntity.getStatusCode().is2xxSuccessful());
                }, throwable -> {
                    countDownLatch.countDown();
                });

        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void testUpdateBeer() throws InterruptedException {
        var newBeerName = "JTs Beer";
        var beerId = 1;
        var countDownLatch = new CountDownLatch(2);

        webClient.put().uri("/api/v2/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                .fromValue(BeerDto.builder()
                        .beerName(newBeerName)
                        .upc("1233455")
                        .beerStyle("PALE_ALE")
                        .price(new BigDecimal("8.99"))
                        .build()))
                .retrieve().toBodilessEntity()
                .subscribe(responseEntity -> {
                    assertThat(responseEntity.getStatusCode().is2xxSuccessful());
                    countDownLatch.countDown();
                });

        //wait for update thread to complete
        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        webClient.get().uri("/api/v2/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(BeerDto.class)
                .subscribe(beer -> {
                    assertThat(beer).isNotNull();
                    assertThat(beer.getBeerName()).isNotNull();
                    assertThat(beer.getBeerName()).isEqualTo(newBeerName);
                    countDownLatch.countDown();
                });

        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void testSaveBeer() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDto = BeerDto.builder()
                .beerName("JTs Beer")
                .upc("1233455")
                .beerStyle("PALE_ALE")
                .price(new BigDecimal("8.99"))
                .build();

        var beerResponseMono = webClient.post().uri("/api/v2/beer")
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(beerDto))
                .retrieve().toBodilessEntity();

        beerResponseMono.publishOn(Schedulers.parallel()).subscribe(responseEntity -> {

            assertThat(responseEntity.getStatusCode().is2xxSuccessful());

            countDownLatch.countDown();
        });

        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void testSaveBeerBadRequest() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDto = BeerDto.builder()
                .price(new BigDecimal("8.99"))
                .build();

        var beerResponseMono = webClient.post().uri("/api/v2/beer")
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(beerDto))
                .retrieve().toBodilessEntity();

        beerResponseMono.subscribe(responseEntity -> {

        }, throwable -> {
            if (throwable.getClass().getName().equals("org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest")){
                WebClientResponseException ex = (WebClientResponseException) throwable;

                if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void getBeerByUPC() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDtoMono = webClient.get().uri("/api/v2/beerUpc/" + BeerLoader.BEER_2_UPC)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
            assertThat(beer).isNotNull();
            assertThat(beer.getBeerName()).isNotNull();

            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void getBeerByUPCNotFound() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDtoMono = webClient.get().uri("/api/v2/beerUpc/4484848393939292")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
        }, throwable -> countDownLatch.countDown());

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void getBeerById() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDtoMono = webClient.get().uri("/api/v2/beer/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
            assertThat(beer).isNotNull();
            assertThat(beer.getBeerName()).isNotNull();

            countDownLatch.countDown();
        });

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }

    @Test
    void getBeerByIdNotFound() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);

        var beerDtoMono = webClient.get().uri("/api/v2/beer/" + 1333)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(BeerDto.class);

        beerDtoMono.subscribe(beer -> {
        }, throwable -> countDownLatch.countDown());

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(0);
    }
}
