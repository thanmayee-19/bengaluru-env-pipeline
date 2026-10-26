package com.example.readingservice.controller;

import com.example.readingservice.repository.ReadingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    private final ReadingRepository repository;

    public HealthController(ReadingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        long count = repository.count();

        return Map.of(
            "status", "UP",
            "database", "CONNECTED",
            "readingCount", count
        );
    }
}
