package com.danilocicvaric.canteen_system.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RestrictionDtos {
    public record CreateRestrictionRequest(
            @NotBlank String startDate,
            @NotBlank String endDate,
            @NotNull List<CanteenDtos.@Valid WorkingHourDto> workingHours
    ) {}

    public record RestrictionResponse(
            String id,
            String startDate,
            String endDate,
            List<CanteenDtos.WorkingHourDto> workingHours
    ) {}
}
