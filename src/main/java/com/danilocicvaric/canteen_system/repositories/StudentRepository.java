package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Student;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<@NonNull Student, @NonNull Long> {

    boolean existsByEmailIgnoreCase(String email);

}