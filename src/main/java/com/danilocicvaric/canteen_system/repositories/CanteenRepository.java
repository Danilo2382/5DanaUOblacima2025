package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Canteen;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanteenRepository extends JpaRepository<@NonNull Canteen, @NonNull Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByLocationIgnoreCase(String location);

    Optional<Canteen> findByNameIgnoreCase(String name);

    Optional<Canteen> findByLocationIgnoreCase(String location);
}