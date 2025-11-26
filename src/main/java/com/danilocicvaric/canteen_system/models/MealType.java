package com.danilocicvaric.canteen_system.models;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum MealType {

    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner");

    private final String value;

    MealType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MealType fromString(String value) {
        if (value == null)
            throw new IllegalArgumentException(ErrorCode.MEAL_TYPE_NULL.getMessageKey());

        String normalized = value.toLowerCase(Locale.ROOT);
        for (MealType type : MealType.values()) {
            if (type.value.equals(normalized))
                return type;
        }

        throw new IllegalArgumentException(ErrorCode.MEAL_TYPE_INVALID.getMessageKey() + ": " + value);
    }
}