package com.danilocicvaric.canteen_system.controllers;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.services.IStudentService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService studentService;

    @PostMapping
    public ResponseEntity<@NonNull StudentResponse> create(@Valid @RequestBody CreateStudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull StudentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getByIdOrThrow(id));
    }

}
