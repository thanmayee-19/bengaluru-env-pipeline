package com.thanmayee.envpipeline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.dto.Pm25AlertResponse;
import com.thanmayee.envpipeline.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Pm25AlertResponse> getAlerts() {
        return alertService.getAlerts();
    }
}