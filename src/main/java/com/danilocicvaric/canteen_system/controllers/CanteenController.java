package com.danilocicvaric.canteen_system.controllers;

import com.danilocicvaric.canteen_system.dtos.CanteenDtos.*;
import com.danilocicvaric.canteen_system.dtos.RestrictionDtos;
import com.danilocicvaric.canteen_system.services.ICanteenService;
import com.danilocicvaric.canteen_system.services.IRestrictionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/canteens")
@RequiredArgsConstructor
public class CanteenController {

    private final ICanteenService canteenService;
    private final IRestrictionService restrictionService;

    @PostMapping
    public ResponseEntity<@NonNull CanteenResponse> create(@RequestHeader("studentId") Long studentId,
                                                           @Valid @RequestBody CreateCanteenRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(canteenService.create(studentId, req));
    }

    @GetMapping
    public ResponseEntity<@NonNull List<CanteenResponse>> list() {
        return ResponseEntity.ok(canteenService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull CanteenResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(canteenService.getByIdOrThrow(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull CanteenResponse> update(@RequestHeader("studentId") Long studentId,
                                             @PathVariable Long id,
                                             @Valid @RequestBody UpdateCanteenRequest req) {
        return ResponseEntity.ok(canteenService.update(studentId, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> delete(@RequestHeader("studentId") Long studentId, @PathVariable Long id) {
        canteenService.delete(studentId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public List<CanteenStatusResponse> statusAll(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                                                             @RequestParam int duration) {
        return canteenService.statusAll(startDate, endDate, startTime, endTime, duration);
    }

    @GetMapping("/{id}/status")
    public CanteenStatusResponse statusOne(@PathVariable Long id,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                                                       @RequestParam int duration) {
        return canteenService.statusOne(id, startDate, endDate, startTime, endTime, duration);
    }

    @PostMapping("/{id}/restrictions")
    public ResponseEntity<RestrictionDtos.@NonNull RestrictionResponse> createRestriction(@RequestHeader("studentId") Long studentId,
                                                                                          @PathVariable Long id,
                                                                                          @Valid @RequestBody RestrictionDtos.CreateRestrictionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restrictionService.create(studentId, id, req));
    }
}
