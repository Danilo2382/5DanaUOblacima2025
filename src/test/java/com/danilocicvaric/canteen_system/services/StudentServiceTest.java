package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.StudentMapper;
import com.danilocicvaric.canteen_system.models.Student;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private CreateStudentRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setId(1L);
        student.setName("Nikola");
        student.setEmail("nikola@example.com");
        student.setAdmin(false);

        createRequest = new CreateStudentRequest("Nikola", "nikola@example.com", false);
    }

    @Test
    void createStudentSuccess() {
        when(studentRepository.existsByEmailIgnoreCase(createRequest.email())).thenReturn(false);
        when(studentMapper.toEntity(createRequest)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(new StudentResponse("1", "Nikola", "nikola@example.com", false));

        StudentResponse response = studentService.create(createRequest);

        assertEquals("1", response.id());
        assertEquals("Nikola", response.name());
        verify(studentRepository).save(student);
    }

    @Test
    void createStudentEmailAlreadyExistsThrowsException() {
        when(studentRepository.existsByEmailIgnoreCase(createRequest.email())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> studentService.create(createRequest));

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getMessageKey(), ex.getMessage());
    }

    @Test
    void getByIdOrThrowSuccess() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponse(student)).thenReturn(new StudentResponse("1", "Nikola", "nikola@example.com", false));

        StudentResponse response = studentService.getByIdOrThrow(1L);

        assertEquals("Nikola", response.name());
    }

    @Test
    void getByIdOrThrowNotFoundThrowsException() {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> studentService.getByIdOrThrow(2L));
    }
}
