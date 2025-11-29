package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.*;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.StudentMapper;
import com.danilocicvaric.canteen_system.models.Student;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String snsTopicArn;

    @Transactional
    public StudentResponse create(CreateStudentRequest req) {
        // Ensure email uniqueness
        if (studentRepository.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessageKey());

        if (req.indexNumber() != null && studentRepository.existsByIndexNumber(req.indexNumber()))
            throw new IllegalArgumentException(ErrorCode.INDEX_NUMBER_EXISTS.getMessageKey());

        // Map DTO to entity and save
        Student student = studentMapper.toEntity(req);
        Student savedStudent = studentRepository.save(student);

        subscribeToNotifications(savedStudent.getEmail());
        return studentMapper.toResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public StudentResponse getByIdOrThrow(Long id) {
        // Retrieve student by ID or throw NotFoundException
        return studentMapper.toResponse(
                studentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.STUDENT_NOT_FOUND.getMessageKey()))
        );
    }

    private void subscribeToNotifications(String email) {
        try {
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .topicArn(snsTopicArn)
                    .build();

            snsClient.subscribe(subscribeRequest);
            System.out.println("Sent SNS subscription request to: " + email);

        } catch (Exception e) {
            System.err.println("Failed to subscribe student to SNS: " + e.getMessage());
        }
    }

}