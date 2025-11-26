package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;

public interface IStudentService {

    StudentResponse create(CreateStudentRequest req);

    StudentResponse getByIdOrThrow(Long id);
}
