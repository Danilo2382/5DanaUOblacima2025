package com.danilocicvaric.canteen_system.services;

import org.springframework.stereotype.Service;

import com.danilocicvaric.canteen_system.repositories.CanteenRepository;
import com.danilocicvaric.canteen_system.repositories.ReservationRepository;
import com.danilocicvaric.canteen_system.repositories.StudentRepository;

@Service
public class UtilService implements IUtilService {
    private final CanteenRepository canteenRepository;
    private final StudentRepository studentRepository;
    private final ReservationRepository reservationRepository;

    public UtilService(CanteenRepository canteenRepository, StudentRepository studentRepository,
            ReservationRepository reservationRepository) {
        this.canteenRepository = canteenRepository;
        this.studentRepository = studentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void clearAllData() {
        canteenRepository.deleteAll();
        studentRepository.deleteAll();
        reservationRepository.deleteAll();
    }

}
