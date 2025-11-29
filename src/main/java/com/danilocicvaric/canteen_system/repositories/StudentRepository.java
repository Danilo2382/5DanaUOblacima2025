package com.danilocicvaric.canteen_system.repositories;

import com.danilocicvaric.canteen_system.models.Student;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<@NonNull Student, @NonNull Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIndexNumber(String indexNumber);

    Student findByEmail(String email);
}