package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.StudentMapper;
import com.danilocicvaric.canteen_system.models.Student;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse create(CreateStudentRequest req) {
        // Ensure email uniqueness
        if (studentRepository.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessageKey());

        // Map DTO to entity and save
        Student student = studentMapper.toEntity(req);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public StudentResponse getByIdOrThrow(Long id) {
        // Retrieve student by ID or throw NotFoundException
        return studentMapper.toResponse(
                studentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND.getMessageKey()))
        );
    }

}