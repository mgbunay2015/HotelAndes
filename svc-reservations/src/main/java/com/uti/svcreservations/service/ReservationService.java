package com.uti.svcreservations.service;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;

import java.util.List;

/**
 * Contrato del servicio de gestion de reservas.
 * Los nombres de metodos siguen la especificacion de la actividad.
 */
public interface ReservationService {

    /**
     * Crea una reserva.
     * RestTemplate: verifica disponibilidad en svc-rooms.
     * WebClient: obtiene datos del cuarto para la respuesta enriquecida.
     */
    ReservationResponse createReservation(ReservationRequest request);

    /**
     * Obtiene una reserva por ID enriquecida con datos del cuarto via RestTemplate.
     */
    ReservationResponse getReservationById(Long id);

    /**
     * Lista reservas de un huesped enriquecidas con datos del cuarto via WebClient.
     */
    List<ReservationResponse> getReservationsByEmail(String email);

    /**
     * Registra checkout de una reserva ACTIVE.
     * RestTemplate: obtiene datos del cuarto para la respuesta enriquecida.
     */
    ReservationResponse checkout(Long id);
}
