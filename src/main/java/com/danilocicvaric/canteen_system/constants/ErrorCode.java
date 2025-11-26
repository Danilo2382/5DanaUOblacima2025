package com.danilocicvaric.canteen_system.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Student errors
    STUDENT_NOT_FOUND("Student not found"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    ONLY_ADMIN_CAN_PERFORM("Only admin student can perform this action"),

    // Canteen errors
    CANTEEN_NOT_FOUND("Canteen not found"),
    CANTEEN_NAME_EXISTS("Canteen name already exists"),
    CANTEEN_LOCATION_EXISTS("Canteen location already exists"),

    // Status query errors
    END_DATE_BEFORE_START("End date cannot be before start date"),
    END_TIME_BEFORE_START("End time cannot be before start time"),
    INVALID_DURATION("Duration must be 30 or 60 minutes"),

    // Meal type errors
    MEAL_TYPE_NULL("Meal type cannot be null"),
    MEAL_TYPE_INVALID("Invalid meal type"),

    // Reservation errors
    RESERVATION_NOT_FOUND("Reservation not found"),
    RESERVATION_IN_PAST("Reservation cannot be in the past"),
    RESERVATION_INVALID_DURATION("Duration must be 30 or 60 minutes"),
    RESERVATION_INVALID_TIME("Time must be on the hour or half hour"),
    RESERVATION_NOT_IN_WORKING_HOURS("Time not within canteen working hours"),
    RESERVATION_NO_CAPACITY("No remaining capacity for selected slot"),
    RESERVATION_STUDENT_OVERLAP("Student already has a reservation in the selected interval"),
    RESERVATION_ONLY_OWNER_CAN_CANCEL("Only reservation owner can cancel it");

    private final String messageKey;
}
