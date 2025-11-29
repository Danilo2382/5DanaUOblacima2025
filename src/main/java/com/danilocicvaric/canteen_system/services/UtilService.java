package com.danilocicvaric.canteen_system.services;

import org.springframework.stereotype.Service;

import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import com.danilocicvaric.canteen_system.repositories.RestrictionRepository;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;

@Service
public class UtilService implements IUtilService {
    private final CanteenRepository canteenRepository;
    private final StudentRepository studentRepository;
    private final ReservationRepository reservationRepository;
    private final RestrictionRepository restrictionRepository;

    public UtilService(CanteenRepository canteenRepository, StudentRepository studentRepository,
            ReservationRepository reservationRepository, RestrictionRepository restrictionRepository) {
        this.canteenRepository = canteenRepository;
        this.studentRepository = studentRepository;
        this.reservationRepository = reservationRepository;
        this.restrictionRepository = restrictionRepository;
    }

    @Override
    public void clearAllData() {
        restrictionRepository.deleteAll();
        reservationRepository.deleteAll();
        canteenRepository.deleteAll();
        studentRepository.deleteAll();
    }
}
