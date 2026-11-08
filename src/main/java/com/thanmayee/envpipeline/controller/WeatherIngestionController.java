package com.thanmayee.envpipeline.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.service.IngestionBenchmarkService;
import com.thanmayee.envpipeline.service.WeatherIngestionService;

@RestController
@RequestMapping("/api/weather")
public class WeatherIngestionController {

    private final WeatherIngestionService ingestionService;
    private final IngestionBenchmarkService benchmarkService;

    public WeatherIngestionController(
            WeatherIngestionService ingestionService,
            IngestionBenchmarkService benchmarkService) {

        this.ingestionService = ingestionService;
        this.benchmarkService = benchmarkService;
    }

    @PostMapping("/ingest")
    public String ingest() {

        int inserted = ingestionService.ingest();

        return "Weather ingestion completed. New readings inserted: "
                + inserted;
    }

    @PostMapping("/benchmark")
    public Map<String, Object> benchmark() {

        return benchmarkService.benchmark();
    }
}
