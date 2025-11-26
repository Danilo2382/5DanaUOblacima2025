package com.danilocicvaric.canteen_system.mappers;

import com.danilocicvaric.canteen_system.dtos.CanteenDtos.*;
import com.danilocicvaric.canteen_system.models.Canteen;
import com.danilocicvaric.canteen_system.models.CanteenWorkingHour;
import com.danilocicvaric.canteen_system.models.MealType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CanteenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workingHours", source = "workingHours")
    Canteen toEntity(CreateCanteenRequest request);

    @Mapping(target = "meal", expression = "java(parseMealType(dto.meal()))")
    @Mapping(target = "fromTime", expression = "java(parseTime(dto.from()))")
    @Mapping(target = "toTime", expression = "java(parseTime(dto.to()))")
    CanteenWorkingHour toWorkingHourEntity(WorkingHourDto dto);

    @Mapping(target = "id", expression = "java(String.valueOf(canteen.getId()))")
    CanteenResponse toCanteenResponse(Canteen canteen);

    @Mapping(target = "meal", expression = "java(wh.getMeal().getValue())")
    @Mapping(target = "from", expression = "java(wh.getFromTime().toString())")
    @Mapping(target = "to", expression = "java(wh.getToTime().toString())")
    WorkingHourDto toWorkingHourDto(CanteenWorkingHour wh);

    default CanteenStatusResponse toCanteenStatusResponse(Canteen canteen, List<CanteenStatusItem> slots) {
        return new CanteenStatusResponse(
                String.valueOf(canteen.getId()),
                slots
        );
    }

    default MealType parseMealType(String meal) {
        return MealType.fromString(meal);
    }

    default LocalTime parseTime(String time) {
        return LocalTime.parse(time);
    }
}