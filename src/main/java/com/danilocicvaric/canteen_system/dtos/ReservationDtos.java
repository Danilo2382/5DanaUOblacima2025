package com.danilocicvaric.canteen_system.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReservationDtos {

    public record CreateReservationRequest(
            @NotBlank String studentId,
            @NotBlank String canteenId,
            @NotBlank String date,
            @NotBlank String time,
            @Min(30) @Max(60) int duration
    ) {}

    public record ReservationResponse(
            String id,
            String status,
            String studentId,
            String canteenId,
            String date,
            String time,
            int duration
    ) {}
}
