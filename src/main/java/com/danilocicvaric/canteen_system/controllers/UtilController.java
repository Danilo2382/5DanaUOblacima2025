package com.danilocicvaric.canteen_system.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danilocicvaric.canteen_system.services.IUtilService;

@RestController
@RequestMapping("/cleanup")
public class UtilController {
    private final IUtilService utilService;

    public UtilController(IUtilService utilService) {
        this.utilService = utilService;
    }

    @RequestMapping
    public void clearAllData() {
        utilService.clearAllData();
    }
}
