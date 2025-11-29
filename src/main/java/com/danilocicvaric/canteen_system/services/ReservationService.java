package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.ReservationDtos.*;
import com.danilocicvaric.canteen_system.exceptions.ForbiddenException;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.ReservationMapper;
import com.danilocicvaric.canteen_system.models.*;
import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import com.danilocicvaric.canteen_system.repositories.RestrictionRepository;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final StudentRepository studentRepository;
    private final CanteenRepository canteenRepository;
    private final RestrictionRepository restrictionRepository;
    private final ReservationMapper mapper;

    @Transactional
    public ReservationResponse create(CreateReservationRequest req) {
        // Parse IDs safely and throw NotFoundException if invalid
        Long studentId = parseId(req.studentId(), ErrorCode.STUDENT_NOT_FOUND);
        Long canteenId = parseId(req.canteenId(), ErrorCode.CANTEEN_NOT_FOUND);

        Student student = findStudentByIdOrThrow(studentId);
        Canteen canteen = findCanteenByIdOrThrow(canteenId);

        LocalDate date = LocalDate.parse(req.date());
        LocalTime time = LocalTime.parse(req.time());
        int duration = req.duration();

        // Validate all business rules for the reservation
        validateReservationRules(canteen, student, date, time, duration);

        // Map DTO to entity and set associations
        Reservation reservation = mapper.toEntity(req);
        reservation.setStudent(student);
        reservation.setCanteen(canteen);

        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancel(Long reservationId, Long studentId) {
        Reservation reservation = findReservationByIdOrThrow(reservationId);

        // Only the student who owns the reservation can cancel it
        if (!reservation.getStudent().getId().equals(studentId))
            throw new ForbiddenException(ErrorCode.RESERVATION_ONLY_OWNER_CAN_CANCEL.getMessageKey());

        reservation.setStatus(ReservationStatus.CANCELLED);
        return mapper.toResponse(reservation);
    }

    // Private helper methods
    private Student findStudentByIdOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND.getMessageKey()));
    }

    private Canteen findCanteenByIdOrThrow(Long id) {
        return canteenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CANTEEN_NOT_FOUND.getMessageKey()));
    }

    private Reservation findReservationByIdOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND.getMessageKey()));
    }

    private void validateReservationRules(Canteen canteen, Student student,
                                          LocalDate date, LocalTime time, int duration) {
        // Run all validation steps for the reservation
        validateNotInPast(date, time);
        validateDuration(duration);
        validateTimeOnHourOrHalfHour(time);
        validateWithinWorkingHours(canteen, date, time, duration);
        validateCapacity(canteen, date, time, duration);
        validateNoStudentOverlap(student, date, time, duration);
        validateMealTypeLimit(student, canteen, date, time);
    }

    private void validateNotInPast(LocalDate date, LocalTime time) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationStart = LocalDateTime.of(date, time);

        if (reservationStart.isBefore(now.withSecond(0).withNano(0)))
            throw new IllegalArgumentException(ErrorCode.RESERVATION_IN_PAST.getMessageKey());
    }

    private void validateDuration(int duration) {
        // Only 30 or 60 minutes are allowed
        if (!(duration == 30 || duration == 60))
            throw new IllegalArgumentException(ErrorCode.RESERVATION_INVALID_DURATION.getMessageKey());
    }

    private void validateTimeOnHourOrHalfHour(LocalTime time) {
        int minute = time.getMinute();
        // Only allow reservations starting on the hour or half-hour
        if (!(minute == 0 || minute == 30))
            throw new IllegalArgumentException(ErrorCode.RESERVATION_INVALID_TIME.getMessageKey());
    }

    private void validateWithinWorkingHours(Canteen canteen, LocalDate date, LocalTime time, int duration) {
        // Must fit within at least one working hour interval
        Optional<Restriction> activeRestriction = restrictionRepository
                .findActiveRestriction(canteen.getId(), date);

        List<CanteenWorkingHour> effectiveHours;

        if (activeRestriction.isPresent()) {
            // If restriction exists, prioritize it over regular working hours
            effectiveHours = activeRestriction.get().getWorkingHours();
        } else {
            effectiveHours = canteen.getWorkingHours();
        }

        boolean fitsInWorkingHours = effectiveHours.stream()
                .anyMatch(wh -> !time.isBefore(wh.getFromTime())
                        && !time.plusMinutes(duration).isAfter(wh.getToTime()));

        if (!fitsInWorkingHours)
            throw new IllegalArgumentException(ErrorCode.RESERVATION_NOT_IN_WORKING_HOURS.getMessageKey());
    }

    private void validateCapacity(Canteen canteen, LocalDate date, LocalTime time, int duration) {
        // Count active reservations for the slot
        long takenSlots = reservationRepository.countActiveByCanteenAndSlot(canteen, date, time, duration);

        if (takenSlots >= canteen.getCapacity())
            throw new IllegalArgumentException(ErrorCode.RESERVATION_NO_CAPACITY.getMessageKey());
    }

    private void validateNoStudentOverlap(Student student, LocalDate date, LocalTime time, int duration) {
        // Ensure the student has no other active reservation that overlaps
        List<Reservation> existingReservations =
                reservationRepository.findByStudentAndDateAndStatus(student, date, ReservationStatus.ACTIVE);

        LocalTime newEnd = time.plusMinutes(duration);

        for (Reservation existing : existingReservations) {
            LocalTime existingStart = existing.getTime();
            LocalTime existingEnd = existing.getTime().plusMinutes(existing.getDurationMinutes());

            boolean overlaps = time.isBefore(existingEnd) && existingStart.isBefore(newEnd);
            if (overlaps)
                throw new IllegalArgumentException(ErrorCode.RESERVATION_STUDENT_OVERLAP.getMessageKey());
        }
    }

    private void validateMealTypeLimit(Student student, Canteen canteen, LocalDate date, LocalTime time) {
        MealType currentMealType = canteen.getWorkingHours().stream()
                .filter(wh -> !time.isBefore(wh.getFromTime()) && !time.isAfter(wh.getToTime()))
                .findFirst()
                .map(CanteenWorkingHour::getMeal)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.RESERVATION_NOT_IN_WORKING_HOURS.getMessageKey()));

        long existingCount = reservationRepository.countByStudentDateAndMealType(student, date, currentMealType);

        if (existingCount >= 2) {
            throw new IllegalArgumentException(ErrorCode.RESERVATION_MEAL_LIMIT_EXCEEDED.getMessageKey());
        }
    }

    private Long parseId(String id, ErrorCode errorCode) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NotFoundException(errorCode.getMessageKey());
        }
    }
}