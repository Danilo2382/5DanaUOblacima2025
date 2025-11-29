package com.danilocicvaric.canteen_system.mappers;

import com.danilocicvaric.canteen_system.dtos.RestrictionDtos.*;
import com.danilocicvaric.canteen_system.dtos.CanteenDtos.WorkingHourDto;
import com.danilocicvaric.canteen_system.models.Restriction;
import com.danilocicvaric.canteen_system.models.CanteenWorkingHour;
import com.danilocicvaric.canteen_system.models.MealType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface RestrictionMapper {
    @Mapping(target = "startDate", expression = "java(parseDate(req.startDate()))")
    @Mapping(target = "endDate", expression = "java(parseDate(req.endDate()))")
    @Mapping(target = "workingHours", source = "workingHours")
    Restriction toEntity(CreateRestrictionRequest req);

    @Mapping(target = "id", expression = "java(String.valueOf(restriction.getId()))")
    @Mapping(target = "startDate", expression = "java(restriction.getStartDate().toString())")
    @Mapping(target = "endDate", expression = "java(restriction.getEndDate().toString())")
    @Mapping(target = "workingHours", source = "workingHours")
    RestrictionResponse toResponse(Restriction restriction);

    @Mapping(target = "meal", expression = "java(parseMealType(dto.meal()))")
    @Mapping(target = "fromTime", expression = "java(parseTime(dto.from()))")
    @Mapping(target = "toTime", expression = "java(parseTime(dto.to()))")
    CanteenWorkingHour toWorkingHourEntity(WorkingHourDto dto);

    @Mapping(target = "meal", expression = "java(wh.getMeal().getValue())")
    @Mapping(target = "from", expression = "java(wh.getFromTime().toString())")
    @Mapping(target = "to", expression = "java(wh.getToTime().toString())")
    WorkingHourDto toWorkingHourDto(CanteenWorkingHour wh);


    default MealType parseMealType(String meal) {
        return MealType.valueOf(meal.toUpperCase());
    }

    default LocalTime parseTime(String time) {
        return LocalTime.parse(time);
    }

    default LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }
}