package com.nazjara.repository;

import com.nazjara.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class PersonRepositoryImplTest {

    PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        var personMono = personRepository.getById(1);

        System.out.println(personMono.block().toString());
    }

    @Test
    void getByIdSubscribe() {
        var personMono = personRepository.getById(1);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void getByIdNotFound() {
        var personMono = personRepository.getById(9);

        StepVerifier.create(personMono).verifyComplete();

        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void getByIdMap() {
        var personMono = personRepository.getById(1);

        personMono
                .map(Person::getFirstName)
                .subscribe(firstName -> System.out.println("FirstName: " + firstName));
    }

    @Test
    void findAllBlockFirst() {
        var personFlux = personRepository.findAll();

        System.out.println(personFlux.blockFirst().toString());
    }

    @Test
    void findAllSubscribe() {
        var personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        personFlux.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void findAllToList() {
        var personFlux = personRepository.findAll();

        personFlux.collectList().subscribe(list -> list.forEach(System.out::println));
    }

    @Test
    void getByIdWithFiltering() {
        var personFlux = personRepository.findAll();

        var personMono = personFlux
                .filter(person -> person.getId() == 3)
                .next();

        personMono.subscribe(System.out::println);
    }

    @Test
    void getByIdWithFilteringNotFound() {
        var personFlux = personRepository.findAll();

        var personMono = personFlux
                .filter(person -> person.getId() == 9)
                .next();

        personMono.subscribe(System.out::println);
    }

    @Test
    void getByIdWithFilteringNotFoundWithException() {
        var personFlux = personRepository.findAll();

        var personMono = personFlux
                .filter(person -> person.getId() == 9)
                .single();

        personMono
                .doOnError(throwable -> System.out.println("Exception happened"))
                .onErrorReturn(Person.builder().build())
                .subscribe(System.out::println);
    }
}
