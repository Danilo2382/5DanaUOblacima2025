package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.CanteenDtos.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ICanteenService {

    CanteenResponse create(Long studentIdHeader, CreateCanteenRequest req);

    List<CanteenResponse> findAll();

    CanteenResponse getByIdOrThrow(Long id);

    CanteenResponse update(Long studentIdHeader, Long canteenId, UpdateCanteenRequest req);

    void delete(Long studentIdHeader, Long canteenId);

    List<CanteenStatusResponse> statusAll(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int duration);

    CanteenStatusResponse statusOne(Long canteenId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int duration);

}
