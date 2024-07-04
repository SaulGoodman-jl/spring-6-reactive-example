package guru.springframework.spring6reactiveexample.repositories;

import guru.springframework.spring6reactiveexample.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> getById(Integer id);

    Flux<Person> findAll();
}
