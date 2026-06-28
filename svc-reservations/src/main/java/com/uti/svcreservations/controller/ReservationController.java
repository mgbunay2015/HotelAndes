package com.uti.svcreservations.controller;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de reservas hoteleras.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Crea una nueva reserva verificando disponibilidad en svc-rooms.
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse created = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtiene una reserva por ID con datos enriquecidos del cuarto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    /**
     * Lista todas las reservas de un huésped por email.
     */
    @GetMapping("/guest/{email}")
    public ResponseEntity<List<ReservationResponse>> findByGuestEmail(@PathVariable String email) {
        return ResponseEntity.ok(reservationService.getReservationsByEmail(email));
    }

    /**
     * Registra la salida (checkout) de una reserva activa.
     */
    @PatchMapping("/{id}/checkout")
    public ResponseEntity<ReservationResponse> checkout(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkout(id));
    }
}
