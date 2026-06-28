package com.uti.svcrooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio svc-rooms.
 * Gestiona el catálogo de habitaciones del Hotel Los Andes.
 */
@SpringBootApplication
public class SvcRoomsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvcRoomsApplication.class, args);
    }
}
