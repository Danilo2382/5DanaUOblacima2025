package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCanteenAndDateAndStatus(Canteen canteen, LocalDate date, ReservationStatus status);

    List<Reservation> findByStudentAndDateAndStatus(Student student, LocalDate date, ReservationStatus status);

    @Query("select count(r) from Reservation r where r.canteen = :canteen and r.date = :date and r.status = 'ACTIVE' and r.time = :time and r.durationMinutes = :duration")
    long countActiveByCanteenAndSlot(@Param("canteen") Canteen canteen,
                                     @Param("date") LocalDate date,
                                     @Param("time") LocalTime time,
                                     @Param("duration") int duration);

    List<Reservation> findByCanteenAndStatus(Canteen canteen, ReservationStatus status);

    List<Reservation> findByCanteenIdAndDateBetween(Long canteenId, LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT COUNT(r) FROM Reservation r
        JOIN r.canteen c
        JOIN c.workingHours wh
        WHERE r.student = :student
          AND r.date = :date
          AND r.status = 'ACTIVE'
          AND wh.meal = :mealType
          AND r.time >= wh.fromTime
          AND r.time < wh.toTime
    """)
    long countByStudentDateAndMealType(@Param("student") Student student,
                                       @Param("date") LocalDate date,
                                       @Param("mealType") MealType mealType);
}
