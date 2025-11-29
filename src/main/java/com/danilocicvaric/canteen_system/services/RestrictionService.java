package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.constants.ErrorCode;
import com.danilocicvaric.canteen_system.dtos.RestrictionDtos.*;
import com.danilocicvaric.canteen_system.dtos.StudentDtos.StudentResponse;
import com.danilocicvaric.canteen_system.exceptions.ForbiddenException;
import com.danilocicvaric.canteen_system.exceptions.NotFoundException;
import com.danilocicvaric.canteen_system.mappers.RestrictionMapper;
import com.danilocicvaric.canteen_system.models.*;
import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import com.danilocicvaric.canteen_system.repositories.RestrictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestrictionService implements IRestrictionService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String snsTopicArn;

    private final RestrictionRepository restrictionRepository;
    private final CanteenRepository canteenRepository;
    private final ReservationRepository reservationRepository;
    private final IStudentService studentService;
    private final RestrictionMapper mapper;

    @Transactional
    public RestrictionResponse create(Long studentIdHeader, Long canteenId, CreateRestrictionRequest req) {
        requireAdmin(studentIdHeader);
        Canteen canteen = findCanteenByIdOrThrow(canteenId);
        LocalDate start = LocalDate.parse(req.startDate());
        LocalDate end = LocalDate.parse(req.endDate());

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date should be after end date");
        }

        boolean overlapExists = restrictionRepository
                .existsByCanteenIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        canteenId, end, start
                );

        if (overlapExists) {
            throw new IllegalArgumentException("A restriction already exists for this canteen in the given period.");
        }

        Restriction restriction = new Restriction();
        restriction.setCanteen(canteen);
        restriction.setStartDate(start);
        restriction.setEndDate(end);

        List<CanteenWorkingHour> tempHours = req.workingHours().stream()
                .map(wh -> new CanteenWorkingHour(
                        MealType.valueOf(wh.meal().toUpperCase()),
                        LocalTime.parse(wh.from()),
                        LocalTime.parse(wh.to())
                ))
                .collect(Collectors.toList());

        restriction.setWorkingHours(tempHours);
        Restriction savedRestriction = restrictionRepository.save(restriction);

        processCancellations(canteen, start, end, tempHours);

        return mapper.toResponse(savedRestriction);
    }

    // --- Helpers ---

    private void processCancellations(Canteen canteen, LocalDate start, LocalDate end, List<CanteenWorkingHour> allowedHours) {
        List<Reservation> reservations = reservationRepository
                .findByCanteenIdAndDateBetween(canteen.getId(), start, end);

        for (Reservation res : reservations) {
            if (res.getStatus() == ReservationStatus.CANCELLED) continue;

            if (shouldCancel(res, allowedHours)) {
                cancelAndNotify(res);
            }
        }
    }

    private boolean shouldCancel(Reservation res, List<CanteenWorkingHour> allowedHours) {
        LocalTime resStart = res.getTime();
        LocalTime resEnd = resStart.plusMinutes(res.getDurationMinutes());

        boolean fitsInAtLeastOneRule = allowedHours.stream()
                .anyMatch(rule -> {
                    boolean startsOk = !resStart.isBefore(rule.getFromTime());

                    boolean endsOk = !resEnd.isAfter(rule.getToTime());

                    return startsOk && endsOk;
                });

        return !fitsInAtLeastOneRule;
    }

    private void cancelAndNotify(Reservation res) {
        String emailBody = String.format(
                "Your reservation in canteen %s at %s has been canceled because of changed working hours. Please book a new reservation.",
                res.getCanteen().getName(),
                res.getDate()
        );
        String subject = "Reservation Cancellation - Canteen System";

        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(snsTopicArn)
                    .message(emailBody)
                    .subject(subject)
                    .build();

            snsClient.publish(request);
            System.out.println("SNS Notification sent to topic for student: " + res.getStudent().getEmail());

        } catch (Exception e) {
            System.err.println("Failed to send cancellation email via SNS: " + e.getMessage());
        }

        res.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(res);
    }

    private void requireAdmin(Long studentId) {
        StudentResponse s = studentService.getByIdOrThrow(studentId);
        if (!s.isAdmin())
            throw new ForbiddenException(ErrorCode.ONLY_ADMIN_CAN_PERFORM.getMessageKey());
    }

    private Canteen findCanteenByIdOrThrow(Long id) {
        return canteenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CANTEEN_NOT_FOUND.getMessageKey()));
    }

    private Long parseId(String id, ErrorCode errorCode) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NotFoundException(errorCode.getMessageKey());
        }
    }
}