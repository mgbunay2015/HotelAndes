package com.uti.svcreservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para deserializar la respuesta de disponibilidad de svc-rooms.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityClientResponse {

    private Long roomId;
    private boolean available;
    private Integer availableRooms;
}
