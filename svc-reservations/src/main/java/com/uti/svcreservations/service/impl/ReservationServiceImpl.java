package com.uti.svcreservations.service.impl;

import com.uti.svcreservations.client.RoomsRestTemplateClient;
import com.uti.svcreservations.client.RoomsWebClient;
import com.uti.svcreservations.dto.AvailabilityClientResponse;
import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.dto.RoomClientResponse;
import com.uti.svcreservations.exception.BadRequestException;
import com.uti.svcreservations.exception.BusinessRuleException;
import com.uti.svcreservations.exception.ResourceNotFoundException;
import com.uti.svcreservations.mapper.ReservationMapper;
import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.ReservationStatus;
import com.uti.svcreservations.repository.ReservationRepository;
import com.uti.svcreservations.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de reservas con comunicación HTTP a svc-rooms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomsRestTemplateClient roomsRestTemplateClient;
    private final RoomsWebClient roomsWebClient;

    /**
     * Crea una reserva verificando disponibilidad (RestTemplate) y enriqueciendo respuesta (WebClient).
     */
    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        log.info("Creating reservation for guest={} roomId={}", request.getGuestEmail(), request.getRoomId());

        validateDates(request);

        if (reservationRepository.existsByGuestEmailAndRoomIdAndStatus(
                request.getGuestEmail(), request.getRoomId(), ReservationStatus.ACTIVE)) {
            throw new BusinessRuleException("Duplicate active reservation: guest already has an active booking for this room");
        }

        AvailabilityClientResponse availability = roomsRestTemplateClient.checkAvailability(request.getRoomId());
        if (availability == null || !availability.isAvailable()) {
            throw new BusinessRuleException("Room is not available");
        }

        RoomClientResponse room = roomsWebClient.getRoomById(request.getRoomId());

        Reservation reservation = reservationMapper.toEntity(request);
        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created with id={}", saved.getId());

        if (availability.getAvailableRooms() != null && availability.getAvailableRooms() > 0) {
            roomsRestTemplateClient.updateAvailability(
                    request.getRoomId(), availability.getAvailableRooms() - 1);
        }

        return reservationMapper.toResponse(saved, room);
    }

    /**
     * Obtiene una reserva enriquecida con datos del cuarto via RestTemplate.
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        log.info("Finding reservation by id={}", id);
        Reservation reservation = findReservationOrThrow(id);
        RoomClientResponse room = roomsRestTemplateClient.getRoomById(reservation.getRoomId());
        return reservationMapper.toResponse(reservation, room);
    }

    /**
     * Lista reservas de un huésped enriquecidas con WebClient.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByEmail(String email) {
        log.info("Finding reservations for guest email={}", email);
        return reservationRepository.findByGuestEmail(email).stream()
                .map(reservation -> {
                    RoomClientResponse room = roomsWebClient.getRoomById(reservation.getRoomId());
                    return reservationMapper.toResponse(reservation, room);
                })
                .toList();
    }

    /**
     * Registra checkout de una reserva ACTIVE y enriquece respuesta via RestTemplate.
     */
    @Override
    @Transactional
    public ReservationResponse checkout(Long id) {
        log.info("Processing checkout for reservation id={}", id);
        Reservation reservation = findReservationOrThrow(id);

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessRuleException("Only active reservations can be checked out");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation id={} marked as COMPLETED", id);

        RoomClientResponse room = roomsRestTemplateClient.getRoomById(updated.getRoomId());
        return reservationMapper.toResponse(updated, room);
    }

    private Reservation findReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private void validateDates(ReservationRequest request) {
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }
}
