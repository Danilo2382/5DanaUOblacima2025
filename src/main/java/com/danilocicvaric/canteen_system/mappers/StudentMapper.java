package com.danilocicvaric.canteen_system.mappers;

import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Bean;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "isAdmin", target = "admin")
    @Mapping(source = "indexNumber", target = "indexNumber")
    Student toEntity(CreateStudentRequest request);

    @Mapping(target = "id", expression = "java(String.valueOf(student.getId()))")
    @Mapping(source = "admin", target = "isAdmin")
    @Mapping(source = "indexNumber", target = "indexNumber")
    StudentResponse toResponse(Student student);
}