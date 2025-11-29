package com.danilocicvaric.canteen_system.services;

import com.danilocicvaric.canteen_system.dtos.RestrictionDtos;

public interface IRestrictionService {
    RestrictionDtos.RestrictionResponse create(Long studentIdHeader, Long canteenId, RestrictionDtos.CreateRestrictionRequest req);

}
