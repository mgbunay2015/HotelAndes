package com.uti.svcreservations.client;

import com.uti.svcreservations.dto.AvailabilityClientResponse;
import com.uti.svcreservations.dto.AvailabilityUpdateClientRequest;
import com.uti.svcreservations.dto.RoomClientResponse;
import com.uti.svcreservations.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP síncrono basado en RestTemplate para consumir svc-rooms.
 * Usado para verificar disponibilidad y obtener datos de habitación en operaciones bloqueantes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoomsRestTemplateClient {

    private final RestTemplate restTemplate;

    @Value("${rooms.service.url}")
    private String roomsServiceUrl;

    public static final String FALLBACK_MESSAGE = "Room information temporarily unavailable";

    /**
     * Consulta disponibilidad de una habitación via RestTemplate.
     * Usado en createReservation() según la actividad.
     */
    @CircuitBreaker(name = "roomsClient", fallbackMethod = "checkAvailabilityFallback")
    @Retry(name = "roomsClient")
    public AvailabilityClientResponse checkAvailability(Long roomId) {
        log.info("RestTemplate: checking availability for room id={}", roomId);
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId + "/availability";
        return restTemplate.getForObject(url, AvailabilityClientResponse.class);
    }

    /**
     * Obtiene datos de una habitación via RestTemplate.
     * Usado en getReservationById() y checkout() según la actividad.
     */
    @CircuitBreaker(name = "roomsClient", fallbackMethod = "getRoomByIdFallback")
    @Retry(name = "roomsClient")
    public RoomClientResponse getRoomById(Long roomId) {
        log.info("RestTemplate: fetching room id={}", roomId);
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId;
        return restTemplate.getForObject(url, RoomClientResponse.class);
    }

    /**
     * Actualiza la disponibilidad de una habitación decrementando el inventario.
     */
    @CircuitBreaker(name = "roomsClient", fallbackMethod = "updateAvailabilityFallback")
    @Retry(name = "roomsClient")
    public AvailabilityClientResponse updateAvailability(Long roomId, Integer availableRooms) {
        log.info("RestTemplate: updating availability for room id={} to {}", roomId, availableRooms);
        String url = roomsServiceUrl + "/api/v1/rooms/" + roomId + "/availability";
        AvailabilityUpdateClientRequest body = AvailabilityUpdateClientRequest.builder()
                .availableRooms(availableRooms)
                .build();
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(body), AvailabilityClientResponse.class)
                .getBody();
    }

    /**
     * Fallback cuando svc-rooms no responde al verificar disponibilidad.
     * Lanza 503 Service Unavailable para impedir reservas sin verificación.
     */
    public AvailabilityClientResponse checkAvailabilityFallback(Long roomId, Throwable t) {
        log.warn("Fallback triggered for availability check, roomId={}, cause={}", roomId, t.getMessage());
        throw new ServiceUnavailableException(FALLBACK_MESSAGE);
    }

    /**
     * Fallback cuando svc-rooms no responde al obtener datos del cuarto.
     * Devuelve mensaje indicando indisponibilidad temporal de información.
     */
    public RoomClientResponse getRoomByIdFallback(Long roomId, Throwable t) {
        log.warn("Fallback triggered for getRoomById, roomId={}, cause={}", roomId, t.getMessage());
        return RoomClientResponse.builder()
                .id(roomId)
                .roomNumber(FALLBACK_MESSAGE)
                .build();
    }

    /**
     * Fallback para actualización de disponibilidad — no bloquea la operación principal.
     */
    public AvailabilityClientResponse updateAvailabilityFallback(Long roomId, Integer availableRooms, Throwable t) {
        log.warn("Fallback triggered for updateAvailability, roomId={}, cause={}", roomId, t.getMessage());
        return null;
    }
}
