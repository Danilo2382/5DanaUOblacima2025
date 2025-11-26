package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.ReservationDtos.*;

public interface IReservationService {

    ReservationResponse create(CreateReservationRequest req);

    ReservationResponse cancel(Long reservationId, Long studentId);
}