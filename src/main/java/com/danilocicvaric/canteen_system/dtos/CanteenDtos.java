package com.danilocicvaric.canteen_system.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CanteenDtos {

    public record CreateCanteenRequest(
            @NotBlank String name,
            @NotBlank String location,
            @Min(1) int capacity,
            @NotNull List<@Valid WorkingHourDto> workingHours
    ) {}

    public record UpdateCanteenRequest(
            String name,
            String location,
            Integer capacity,
            List<WorkingHourDto> workingHours
    ) {}

    public record WorkingHourDto(
            @NotBlank String meal,
            @NotBlank String from,
            @NotBlank String to
    ) {}

    public record CanteenResponse(
            String id,
            String name,
            String location,
            int capacity,
            List<WorkingHourDto> workingHours
    ) {}

    public record CanteenStatusItem(
            String date,
            String meal,
            String startTime,
            int remainingCapacity
    ) {}

    public record CanteenStatusResponse(
            String canteenId,
            List<CanteenStatusItem> slots
    ) {}
}
