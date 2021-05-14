package com.nazjara.service;

import com.nazjara.domain.Movie;
import com.nazjara.domain.MovieEvent;
import com.nazjara.repository.MovieRepository;
import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Mono<Movie> findById(String id) {
        return movieRepository.findById(id);
    }

    @Override
    public Flux<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Flux<MovieEvent> streamMovieEvents(String id) {
        return Flux.<MovieEvent>generate(movieEventSynchronousSink ->
                movieEventSynchronousSink
                        .next(MovieEvent.builder().movieId(id).movieDate(new Date()).build()))
                .delayElements(Duration.ofSeconds(1));
    }
}
