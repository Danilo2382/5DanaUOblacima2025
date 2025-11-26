package com.danilocicvaric.canteen_system.mappers;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "isAdmin", target = "admin")
    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "id", expression = "java(String.valueOf(student.getId()))")
    @Mapping(source = "admin", target = "isAdmin")
    StudentResponse toResponse(Student student);
}