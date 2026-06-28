package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.RoomClientResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente HTTP reactivo basado en WebClient para consumir svc-rooms.
 * WebClient es no bloqueante y permite programación reactiva con Mono/Flux.
 * En este proyecto se usa block() para integrarlo con la capa de servicio síncrona.
 */
@Component
@Slf4j
public class RoomsWebClient {

    private final WebClient webClient;

    public RoomsWebClient(@Qualifier("roomsServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene datos de una habitación via WebClient.
     * Usado en createReservation() y getReservationsByEmail() según la actividad.
     */
    @CircuitBreaker(name = "roomsClient", fallbackMethod = "getRoomByIdFallback")
    @Retry(name = "roomsClient")
    public RoomClientResponse getRoomById(Long roomId) {
        log.info("WebClient: fetching room id={}", roomId);
        return webClient.get()
                .uri("/api/v1/rooms/{id}", roomId)
                .retrieve()
                .bodyToMono(RoomClientResponse.class)
                .block();
    }

    /**
     * Fallback cuando svc-rooms no responde — devuelve mensaje de indisponibilidad temporal.
     */
    public RoomClientResponse getRoomByIdFallback(Long roomId, Throwable t) {
        log.warn("WebClient fallback triggered for room id={}, cause={}", roomId, t.getMessage());
        return RoomClientResponse.builder()
                .id(roomId)
                .roomNumber(RoomsRestTemplateClient.FALLBACK_MESSAGE)
                .build();
    }
}
