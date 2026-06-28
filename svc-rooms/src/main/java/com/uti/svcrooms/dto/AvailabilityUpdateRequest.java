package com.uti.svcrooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para actualizar la disponibilidad de una habitación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityUpdateRequest {

    @NotNull(message = "Available rooms is required")
    @Min(value = 0, message = "Available rooms cannot be negative")
    private Integer availableRooms;
}
