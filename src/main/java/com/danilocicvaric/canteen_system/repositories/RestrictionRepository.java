package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
    boolean existsByCanteenIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long canteenId, LocalDate endDate, LocalDate startDate
    );

    @Query("SELECT r FROM Restriction r WHERE r.canteen.id = :canteenId AND :date >= r.startDate AND :date <= r.endDate")
    Optional<Restriction> findActiveRestriction(Long canteenId, LocalDate date);
}