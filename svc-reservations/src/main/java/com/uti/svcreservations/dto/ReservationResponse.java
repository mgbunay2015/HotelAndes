package com.uti.svcreservations.dto;

import com.uti.svcreservations.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida enriquecido con datos de la habitación obtenidos de svc-rooms.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private Long roomId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalNights;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    private String roomNumber;
    private String roomType;
    private Double pricePerNight;
}
