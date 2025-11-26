package com.danilocicvaric.canteen_system.mappers;

import com.danilocicvaric.canteen_system.dtos.ReservationDtos.*;
import com.danilocicvaric.canteen_system.models.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "canteen", ignore = true)
    @Mapping(target = "date", expression = "java(parseDate(request.date()))")
    @Mapping(target = "time", expression = "java(parseTime(request.time()))")
    @Mapping(target = "durationMinutes", source = "duration")
    Reservation toEntity(CreateReservationRequest request);

    @Mapping(target = "id", expression = "java(String.valueOf(reservation.getId()))")
    @Mapping(target = "status", expression = "java(capitalize(reservation.getStatus().name()))")
    @Mapping(target = "studentId", expression = "java(String.valueOf(reservation.getStudent().getId()))")
    @Mapping(target = "canteenId", expression = "java(reservation.getCanteen() != null ? String.valueOf(reservation.getCanteen().getId()) : null)")
    @Mapping(target = "date", expression = "java(reservation.getDate().toString())")
    @Mapping(target = "time", expression = "java(reservation.getTime().toString())")
    @Mapping(target = "duration", source = "durationMinutes")
    ReservationResponse toResponse(Reservation reservation);

    default String capitalize(String status) {
        if (status == null || status.isEmpty()) return status;
        String lower = status.toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    default LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }

    default LocalTime parseTime(String time) {
        return LocalTime.parse(time);
    }
}