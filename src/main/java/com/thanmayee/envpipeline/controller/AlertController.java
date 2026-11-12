package com.thanmayee.envpipeline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @GetMapping
    public List<Object> getAlerts() {
        return List.of();
    }
}
