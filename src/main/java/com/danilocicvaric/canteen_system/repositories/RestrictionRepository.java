package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
    boolean existsByCanteenIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long canteenId, LocalDate endDate, LocalDate startDate
    );
}