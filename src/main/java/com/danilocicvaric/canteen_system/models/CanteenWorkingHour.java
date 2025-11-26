package com.danilocicvaric.canteen_system.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Embeddable
@Getter
@Setter
public class CanteenWorkingHour {

    @NotNull
    @Enumerated(EnumType.STRING)
    private MealType meal;

    @NotNull
    private LocalTime fromTime;

    @NotNull
    private LocalTime toTime;

}
