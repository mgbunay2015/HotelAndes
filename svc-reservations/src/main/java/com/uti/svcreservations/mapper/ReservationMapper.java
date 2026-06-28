package com.uti.svcreservations.mapper;

import com.uti.svcreservations.dto.ReservationRequest;
import com.uti.svcreservations.dto.ReservationResponse;
import com.uti.svcreservations.dto.RoomClientResponse;
import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.ReservationStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidades Reservation y sus DTOs enriquecidos.
 */
@Component
public class ReservationMapper {

    /**
     * Convierte un ReservationRequest en entidad Reservation con estado ACTIVE.
     */
    public Reservation toEntity(ReservationRequest request) {
        return Reservation.builder()
                .roomId(request.getRoomId())
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(ReservationStatus.ACTIVE)
                .build();
    }

    /**
     * Convierte una reserva y datos del cuarto en ReservationResponse enriquecido.
     */
    public ReservationResponse toResponse(Reservation reservation, RoomClientResponse room) {
        ReservationResponse.ReservationResponseBuilder builder = ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoomId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalNights(reservation.getTotalNights())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt());

        if (room != null) {
            builder.roomNumber(room.getRoomNumber())
                    .roomType(room.getType())
                    .pricePerNight(room.getPricePerNight());
        }

        return builder.build();
    }
}
