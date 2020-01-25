package com.example.reactive_spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringApplication.class, args);
    }
}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
    @Id
    private String id;
    private String name;
}

@Component
@RequiredArgsConstructor
@Log4j2
class SimpleDataInitializer {
    private final ReservationRepository reservationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializer() {
        Flux<Reservation> saved = Flux
            .just("Hello", "data", "antonio", "Mirabal", "perretes", "nimiedad")
            .map(name -> new Reservation(null, name))
            .flatMap(reservationRepository::save);

        reservationRepository
            .deleteAll()
            .thenMany(saved)
            .thenMany(this.reservationRepository.findAll())
            .subscribe(log::info);
    }

}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {

}

