package com.thanmayee.envpipeline.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.service.WeatherIngestionService;

@RestController
@RequestMapping("/api/weather")
public class WeatherIngestionController {

    private final WeatherIngestionService ingestionService;

    public WeatherIngestionController(
            WeatherIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest")
    public String ingest() {
        int inserted = ingestionService.ingest();
        return "Weather ingestion completed. New readings inserted: "
                + inserted;
    }
}
