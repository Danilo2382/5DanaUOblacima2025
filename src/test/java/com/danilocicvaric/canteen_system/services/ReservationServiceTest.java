package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.ReservationDtos.CreateReservationRequest;
import com.danilocicvaric.canteen_system.dtos.ReservationDtos.ReservationResponse;
import com.danilocicvaric.canteen_system.exceptions.ForbiddenException;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.ReservationMapper;
import com.danilocicvaric.canteen_system.models.*;
import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CanteenRepository canteenRepository;
    @Mock
    private ReservationMapper mapper;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void create_ShouldCreateReservation_WhenAllRulesAreValid() {
        String dateStr = LocalDate.now().plusDays(1).toString();
        String timeStr = "12:00";
        CreateReservationRequest req = new CreateReservationRequest("1", "1", dateStr, timeStr, 30);

        Student student = new Student();
        student.setId(1L);

        Canteen canteen = new Canteen();
        canteen.setId(1L);
        canteen.setCapacity(10);
        CanteenWorkingHour workingHour = new CanteenWorkingHour();
        workingHour.setFromTime(LocalTime.of(10, 0));
        workingHour.setToTime(LocalTime.of(14, 0));
        canteen.setWorkingHours(List.of(workingHour));

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setStudent(student);
        reservation.setCanteen(canteen);
        reservation.setDate(LocalDate.parse(dateStr));
        reservation.setTime(LocalTime.parse(timeStr));

        ReservationResponse expectedResponse = new ReservationResponse(
                "100", "Active", "1", "1", dateStr, timeStr, 30
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(canteen));
        when(reservationRepository.countActiveByCanteenAndSlot(any(), any(), any(), anyInt())).thenReturn(5L);
        when(reservationRepository.findByStudentAndDateAndStatus(any(), any(), any())).thenReturn(Collections.emptyList());

        when(mapper.toEntity(req)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(mapper.toResponse(reservation)).thenReturn(expectedResponse);

        ReservationResponse result = reservationService.create(req);

        assertNotNull(result);
        assertEquals("100", result.id());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void createShouldThrowNotFoundWhenStudentDoesNotExist() {
        CreateReservationRequest req = new CreateReservationRequest("99", "1", "2025-01-01", "12:00", 30);
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowNotFoundWhenCanteenDoesNotExist() {
        CreateReservationRequest req = new CreateReservationRequest("1", "99", "2025-01-01", "12:00", 30);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.CANTEEN_NOT_FOUND.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenDateIsInPast() {
        String pastDate = LocalDate.now().minusDays(1).toString();
        CreateReservationRequest req = new CreateReservationRequest("1", "1", pastDate, "12:00", 30);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(new Canteen()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_IN_PAST.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenDurationIsInvalid() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        CreateReservationRequest req = new CreateReservationRequest("1", "1", futureDate, "12:00", 45);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(new Canteen()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_INVALID_DURATION.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenTimeIsNotOnHourOrHalfHour() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        CreateReservationRequest req = new CreateReservationRequest("1", "1", futureDate, "12:15", 30);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(new Canteen()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_INVALID_TIME.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenOutsideWorkingHours() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        String timeStr = "18:00"; // Late evening
        CreateReservationRequest req = new CreateReservationRequest("1", "1", futureDate, timeStr, 30);

        Canteen canteen = new Canteen();
        canteen.setId(1L);
        CanteenWorkingHour wh = new CanteenWorkingHour();
        wh.setFromTime(LocalTime.of(10, 0));
        wh.setToTime(LocalTime.of(15, 0));
        canteen.setWorkingHours(List.of(wh));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(canteen));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_NOT_IN_WORKING_HOURS.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenNoCapacity() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        CreateReservationRequest req = new CreateReservationRequest("1", "1", futureDate, "12:00", 30);

        Canteen canteen = new Canteen();
        canteen.setCapacity(5);
        CanteenWorkingHour wh = new CanteenWorkingHour();
        wh.setFromTime(LocalTime.of(8, 0));
        wh.setToTime(LocalTime.of(16, 0));
        canteen.setWorkingHours(List.of(wh));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student()));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(canteen));

        when(reservationRepository.countActiveByCanteenAndSlot(any(), any(), any(), anyInt())).thenReturn(5L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_NO_CAPACITY.getMessageKey(), ex.getMessage());
    }

    @Test
    void createShouldThrowExceptionWhenStudentHasOverlappingReservation() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        CreateReservationRequest req = new CreateReservationRequest("1", "1", futureDate, "12:30", 30);

        Student student = new Student();
        Canteen canteen = new Canteen();
        canteen.setCapacity(100);
        CanteenWorkingHour wh = new CanteenWorkingHour();
        wh.setFromTime(LocalTime.of(8, 0));
        wh.setToTime(LocalTime.of(16, 0));
        canteen.setWorkingHours(List.of(wh));

        Reservation existing = new Reservation();
        existing.setTime(LocalTime.of(12, 0));
        existing.setDurationMinutes(60);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(canteenRepository.findById(1L)).thenReturn(Optional.of(canteen));
        when(reservationRepository.countActiveByCanteenAndSlot(any(), any(), any(), anyInt())).thenReturn(0L);
        when(reservationRepository.findByStudentAndDateAndStatus(student, LocalDate.parse(futureDate), ReservationStatus.ACTIVE))
                .thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.create(req));
        assertEquals(ErrorCode.RESERVATION_STUDENT_OVERLAP.getMessageKey(), ex.getMessage());
    }

    @Test
    void cancelShouldCancelReservationWhenUserIsOwner() {
        Long reservationId = 100L;
        Long studentId = 1L;

        Student owner = new Student();
        owner.setId(studentId);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setStudent(owner);
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ReservationResponse mockResponse = new ReservationResponse("100", "Cancelled", "1", "1", "2025-01-01", "12:00", 30);
        when(mapper.toResponse(reservation)).thenReturn(mockResponse);

        ReservationResponse result = reservationService.cancel(reservationId, studentId);

        assertEquals("Cancelled", result.status());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void cancelShouldThrowNotFoundWhenReservationDoesNotExist() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> reservationService.cancel(100L, 1L));
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND.getMessageKey(), ex.getMessage());
    }

    @Test
    void cancelShouldThrowForbiddenWhenUserIsNotOwner() {
        Long reservationId = 100L;
        Long requesterId = 2L;
        Long ownerId = 1L;

        Student owner = new Student();
        owner.setId(ownerId);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setStudent(owner);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> reservationService.cancel(reservationId, requesterId));
        assertEquals(ErrorCode.RESERVATION_ONLY_OWNER_CAN_CANCEL.getMessageKey(), ex.getMessage());
    }
}