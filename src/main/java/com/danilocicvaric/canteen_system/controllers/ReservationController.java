package com.danilocicvaric.canteen_system.controllers;

import com.danilocicvaric.canteen_system.dtos.ReservationDtos.*;
import com.danilocicvaric.canteen_system.services.IReservationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<@NonNull ReservationResponse> create(@Valid @RequestBody CreateReservationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull ReservationResponse> cancel(@PathVariable Long id,
                                                               @RequestHeader("studentId") Long studentId) {
        return ResponseEntity.ok(reservationService.cancel(id, studentId));
    }
}