package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.CanteenDtos.*;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.StudentResponse;
import com.danilocicvaric.canteen_system.exceptions.ForbiddenException;
import com.danilocicvaric.canteen_system.models.Canteen;
import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.mappers.CanteenMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CanteenServiceTest {

    @Mock private CanteenRepository canteenRepository;
    @Mock private IStudentService studentService;
    @Mock private CanteenMapper mapper;

    @InjectMocks private CanteenService canteenService;

    private StudentResponse adminStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminStudent = new StudentResponse("1","Admin","admin@etf.com", true);
    }

    @Test
    void createCanteenRequiresAdminSuccess() {
        CreateCanteenRequest req = new CreateCanteenRequest("TestCanteen","Location",100, Collections.emptyList());
        when(studentService.getByIdOrThrow(1L)).thenReturn(adminStudent);
        Canteen c = new Canteen();
        when(mapper.toEntity(req)).thenReturn(c);
        when(canteenRepository.save(c)).thenReturn(c);
        when(mapper.toCanteenResponse(c)).thenReturn(new CanteenResponse(Long.toString(1L),"TestCanteen","Location",100, Collections.emptyList()));

        CanteenResponse response = canteenService.create(1L, req);
        assertEquals("TestCanteen", response.name());
    }

    @Test
    void createCanteenNonAdminThrowsForbidden() {
        StudentResponse nonAdmin = new StudentResponse("2","User","user@etf.com", false);
        when(studentService.getByIdOrThrow(2L)).thenReturn(nonAdmin);
        CreateCanteenRequest req = new CreateCanteenRequest("Test","Loc",50, Collections.emptyList());

        assertThrows(ForbiddenException.class, () -> canteenService.create(2L, req));
    }
}
