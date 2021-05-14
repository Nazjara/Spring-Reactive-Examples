package com.nazjara.service;

import com.nazjara.domain.Movie;
import com.nazjara.domain.MovieEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
    Mono<Movie> findById(String id);
    Flux<Movie> findAll();
    Flux<MovieEvent> streamMovieEvents(String id);
}
