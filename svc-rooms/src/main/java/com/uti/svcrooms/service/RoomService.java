package com.uti.svcrooms.service;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.AvailabilityUpdateRequest;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;

import java.util.List;

/**
 * Contrato del servicio de gestión de habitaciones.
 */
public interface RoomService {

    List<RoomResponse> findAll();

    RoomResponse findById(Long id);

    RoomResponse create(RoomRequest request);

    AvailabilityResponse getAvailability(Long id);

    AvailabilityResponse updateAvailability(Long id, AvailabilityUpdateRequest request);
}
