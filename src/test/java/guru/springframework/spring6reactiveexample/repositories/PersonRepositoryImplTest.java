package guru.springframework.spring6reactiveexample.repositories;

import guru.springframework.spring6reactiveexample.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepository personRepository = new PersonRepositoryImpl();

    Mono<Person> personMono;
    Flux<Person> personFlux;

    @BeforeEach
    void setUp() {
        personMono = personRepository.getById(1);
        personFlux = personRepository.findAll();
    }

    @Test
    void testMonoByIdBlock() {
        Person person = personMono.block();
        System.out.println(person.toString());
    }

    @Test
    void testGetByIdSubscriber() {
        personMono.subscribe(person -> {
                    System.out.println(person.toString());
                }
        );
    }

    @Test
    void testMapOperation() {
        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxBlockFirst() {
        Person person = personFlux.blockFirst();
        System.out.println(person.getFirstName());
    }

    @Test
    void testFluxSubscriber() {
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxMap() {
        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Mono<List<Person>> listMono = personFlux.collectList();
        listMono.subscribe(list -> {
            list.forEach(person -> System.out.println(person.getFirstName()));
        });
    }

    @Test
    void testFilterOnName() {
        personRepository.findAll()
                .filter(person -> person.getFirstName().equals("Fiona"))
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testGetById() {
        Mono<Person> fionaMono = personRepository.findAll().filter(person -> person.getFirstName().equals("Fiona")).next();
        fionaMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testGetByIdFound() {
        Mono<Person> byId = personRepository.getById(3);
        assertTrue(byId.hasElement().block());
    }

    @Test
    void testGetByIdFoundStepVerifier() {
        Mono<Person> byId = personRepository.getById(3);
        StepVerifier.create(byId).expectNextCount(1).verifyComplete();

        byId.subscribe(person -> {
            System.out.println(person.getFirstName());
        });
    }


    @Test
    void testFindPersonByIdNotFound() {
        final Integer id = 3;
        Mono<Person> personMono = personFlux.filter(person -> Objects.equals(person.getId(), id)).single().doOnError(
                throwable -> {
                    System.out.println("Error occurred in flux");
                    System.out.println(throwable.toString());
                }
        );

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        }, throwable -> {
            System.out.println("Error Occurred in mono");
            System.out.println(throwable.toString());
        });
    }
}