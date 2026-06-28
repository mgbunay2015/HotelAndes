package com.uti.svcreservations.service;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;

import java.util.List;

/**
 * Contrato del servicio de gestión de reservas.
 */
public interface ReservationService {

    ReservationResponse create(ReservationRequest request);

    ReservationResponse findById(Long id);

    List<ReservationResponse> findByGuestEmail(String email);

    ReservationResponse checkout(Long id);
}
