package com.uti.svcreservations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio svc-reservations.
 * Gestiona las reservas de huéspedes del Hotel Los Andes.
 */
@SpringBootApplication
public class SvcReservationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvcReservationsApplication.class, args);
    }
}
