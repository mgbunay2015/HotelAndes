package com.uti.svcreservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para deserializar los datos de habitación obtenidos de svc-rooms.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomClientResponse {

    private Long id;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private Integer totalCapacity;
    private Integer availableRooms;
    private Integer floor;
    private String description;
}
