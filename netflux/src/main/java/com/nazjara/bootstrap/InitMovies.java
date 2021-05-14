package com.nazjara.bootstrap;

import com.nazjara.domain.Movie;
import com.nazjara.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class InitMovies implements CommandLineRunner {
    private final MovieRepository movieRepository;

    @Override
    public void run(String... args) {
        movieRepository.deleteAll().thenMany(Flux.just("Movie1", "Movie2", "Movie3", "Movie4", "Movie5", "Movie6",
                "Movie7", "Movie8", "Movie9", "Movie10")
                .map(title -> Movie.builder().title(title).build())
                .flatMap(movieRepository::save))
        .subscribe(null, null, () -> movieRepository.findAll().subscribe(System.out::println));
    }
}
