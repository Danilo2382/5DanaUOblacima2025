package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.CanteenDtos.*;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.StudentResponse;
import com.danilocicvaric.canteen_system.exceptions.ForbiddenException;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.CanteenMapper;
import com.danilocicvaric.canteen_system.models.*;
import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CanteenService implements ICanteenService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CanteenRepository canteenRepository;
    private final IStudentService studentService;
    private final ReservationRepository reservationRepository;
    private final CanteenMapper mapper;

    // Ensure that the student making the request is an admin
    private void requireAdmin(Long studentId) {
        StudentResponse s = studentService.getByIdOrThrow(studentId);
        if (!s.isAdmin())
            throw new ForbiddenException(ErrorCode.ONLY_ADMIN_CAN_PERFORM.getMessageKey());
    }

    @Transactional
    public CanteenResponse create(Long studentIdHeader, CreateCanteenRequest req) {
        requireAdmin(studentIdHeader); // Only admins can create canteens
        validateUniqueNameForCreate(req.name()); // Validate name uniqueness
        validateUniqueLocationForCreate(req.location()); // Validate location uniqueness

        Canteen canteen = mapper.toEntity(req);
        return mapper.toCanteenResponse(canteenRepository.save(canteen)); // Save and map to response
    }

    @Transactional(readOnly = true)
    public List<CanteenResponse> findAll() {
        return canteenRepository.findAll().stream()
                .map(mapper::toCanteenResponse)
                .toList(); // Map all canteens to DTO
    }

    @Transactional(readOnly = true)
    public CanteenResponse getByIdOrThrow(Long id) {
        return mapper.toCanteenResponse(findCanteenByIdOrThrow(id)); // Throws if not found
    }

    @Transactional
    public CanteenResponse update(Long studentIdHeader, Long canteenId, UpdateCanteenRequest req) {
        requireAdmin(studentIdHeader); // Only admins can update
        Canteen c = findCanteenByIdOrThrow(canteenId);

        // Update fields if provided, validating uniqueness where necessary
        if (req.name() != null) {
            validateUniqueNameForUpdate(req.name(), canteenId);
            c.setName(req.name());
        }

        if (req.location() != null) {
            validateUniqueLocationForUpdate(req.location(), canteenId);
            c.setLocation(req.location());
        }

        if (req.capacity() != null)
            c.setCapacity(req.capacity());

        if (req.workingHours() != null && !req.workingHours().isEmpty())
            c.setWorkingHours(req.workingHours().stream()
                    .map(mapper::toWorkingHourEntity)
                    .toList());

        return mapper.toCanteenResponse(canteenRepository.save(c));
    }

    @Transactional
    public void delete(Long studentIdHeader, Long canteenId) {
        requireAdmin(studentIdHeader); // Only admins can delete
        Canteen c = findCanteenByIdOrThrow(canteenId);

        cancelActiveReservations(c); // Cancel all active reservations before deletion
        canteenRepository.delete(c);
    }

    @Transactional(readOnly = true)
    public List<CanteenStatusResponse> statusAll(LocalDate startDate, LocalDate endDate,
                                                 LocalTime startTime, LocalTime endTime, int duration) {
        validateStatusQueryParameters(startDate, endDate, startTime, endTime, duration);

        // Compute availability for all canteens
        return canteenRepository.findAll().stream()
                .map(c -> mapper.toCanteenStatusResponse(c,
                        computeSlots(c, startDate, endDate, startTime, endTime, duration)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CanteenStatusResponse statusOne(Long canteenId, LocalDate startDate, LocalDate endDate,
                                           LocalTime startTime, LocalTime endTime, int duration) {
        validateStatusQueryParameters(startDate, endDate, startTime, endTime, duration);
        Canteen c = findCanteenByIdOrThrow(canteenId);

        return mapper.toCanteenStatusResponse(c,
                computeSlots(c, startDate, endDate, startTime, endTime, duration));
    }

    // Private helper methods
    private void validateUniqueNameForCreate(String name) {
        if (canteenRepository.existsByNameIgnoreCase(name))
            throw new IllegalArgumentException(ErrorCode.CANTEEN_NAME_EXISTS.getMessageKey());
    }

    private void validateUniqueLocationForCreate(String location) {
        if (canteenRepository.existsByLocationIgnoreCase(location))
            throw new IllegalArgumentException(ErrorCode.CANTEEN_LOCATION_EXISTS.getMessageKey());
    }

    private void validateUniqueNameForUpdate(String name, Long excludeId) {
        canteenRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(excludeId))
                        throw new IllegalArgumentException(ErrorCode.CANTEEN_NAME_EXISTS.getMessageKey());
                });
    }

    private void validateUniqueLocationForUpdate(String location, Long excludeId) {
        canteenRepository.findByLocationIgnoreCase(location)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(excludeId))
                        throw new IllegalArgumentException(ErrorCode.CANTEEN_LOCATION_EXISTS.getMessageKey());
                });
    }

    private void validateStatusQueryParameters(LocalDate startDate, LocalDate endDate,
                                               LocalTime startTime, LocalTime endTime, int duration) {
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException(ErrorCode.END_DATE_BEFORE_START.getMessageKey());
        if (endTime.isBefore(startTime))
            throw new IllegalArgumentException(ErrorCode.END_TIME_BEFORE_START.getMessageKey());
        if (!(duration == 30 || duration == 60))
            throw new IllegalArgumentException(ErrorCode.INVALID_DURATION.getMessageKey());
    }

    private Canteen findCanteenByIdOrThrow(Long id) {
        return canteenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CANTEEN_NOT_FOUND.getMessageKey()));
    }

    private void cancelActiveReservations(Canteen c) {
        // Cancel all active reservations before deleting the canteen
        reservationRepository.findByCanteenAndStatus(c, ReservationStatus.ACTIVE)
                .forEach(r -> {
                    r.setStatus(ReservationStatus.CANCELLED);
                    r.setCanteen(null);
                });
    }

    private List<CanteenStatusItem> computeSlots(Canteen c, LocalDate startDate, LocalDate endDate,
                                                 LocalTime startTime, LocalTime endTime, int duration) {
        List<CanteenStatusItem> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1))
            result.addAll(computeSlotsForDate(c, date, startTime, endTime, duration));

        sortSlots(result);
        return result;
    }

    private List<CanteenStatusItem> computeSlotsForDate(Canteen c, LocalDate date,
                                                        LocalTime startTime, LocalTime endTime, int duration) {
        List<CanteenStatusItem> slots = new ArrayList<>();

        for (CanteenWorkingHour wh : c.getWorkingHours())
            slots.addAll(computeSlotsForWorkingHour(c, date, wh, startTime, endTime, duration));

        return slots;
    }

    private List<CanteenStatusItem> computeSlotsForWorkingHour(Canteen c, LocalDate date,
                                                               CanteenWorkingHour wh,
                                                               LocalTime startTime, LocalTime endTime,
                                                               int duration) {
        List<CanteenStatusItem> slots = new ArrayList<>();
        LocalTime slotStart = max(startTime, wh.getFromTime());
        LocalTime slotEnd = min(endTime, wh.getToTime());

        for (LocalTime t = alignToInterval(slotStart, duration);
             !t.isAfter(slotEnd.minusMinutes(duration));
             t = t.plusMinutes(duration)) {

            slots.add(createSlotItem(c, date, wh, t, duration));
        }

        return slots;
    }

    private CanteenStatusItem createSlotItem(Canteen c, LocalDate date, CanteenWorkingHour wh,
                                             LocalTime time, int duration) {
        // Count how many active reservations exist for this slot
        long taken = reservationRepository.countActiveByCanteenAndSlot(c, date, time, duration);
        int remaining = Math.max(0, c.getCapacity() - (int) taken);

        return new CanteenStatusItem(
                DATE_FORMATTER.format(date),
                wh.getMeal().name().toLowerCase(Locale.ROOT),
                time.toString(),
                remaining
        );
    }

    private void sortSlots(List<CanteenStatusItem> slots) {
        slots.sort(Comparator.comparing(CanteenStatusItem::date)
                .thenComparing(CanteenStatusItem::meal)
                .thenComparing(CanteenStatusItem::startTime));
    }

    private static LocalTime alignToInterval(LocalTime t, int intervalMinutes) {
        int minute = t.getMinute();

        // Align the time to the nearest 30 or 60-minute intervals
        if (intervalMinutes == 30) {
            if (minute == 0 || minute == 30)
                return t.withSecond(0).withNano(0);
            return t.withMinute(minute < 30 ? 30 : 0)
                    .withSecond(0).withNano(0)
                    .plusHours(minute < 30 ? 0 : 1);
        } else {
            if (minute == 0)
                return t.withSecond(0).withNano(0);
            return t.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        }
    }

    private static LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalTime min(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }
}
