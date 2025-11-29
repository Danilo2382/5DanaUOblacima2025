package com.danilocicvaric.canteen_system.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StudentDtos {

    public record CreateStudentRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank String indexNumber,
            boolean isAdmin
    ) {}

    public record StudentResponse(
            String id,
            String name,
            String email,
            String indexNumber,
            boolean isAdmin
    ) {}
}
