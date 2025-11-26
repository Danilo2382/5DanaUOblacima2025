package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Canteen;
import com.danilocicvaric.canteen_system.models.Reservation;
import com.danilocicvaric.canteen_system.models.ReservationStatus;
import com.danilocicvaric.canteen_system.models.Student;
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
}
