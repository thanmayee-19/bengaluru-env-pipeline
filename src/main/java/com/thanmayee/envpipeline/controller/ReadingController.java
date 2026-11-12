package com.thanmayee.envpipeline.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thanmayee.envpipeline.dto.ReadingPageResponse;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.service.LatestReadingService;
import com.thanmayee.envpipeline.service.ReadingQueryService;
import com.thanmayee.envpipeline.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {

    private final ReadingQueryService readingQueryService;
    private final LatestReadingService latestReadingService;

    public ReadingController(
            ReadingQueryService readingQueryService,
            LatestReadingService latestReadingService) {

        this.readingQueryService = readingQueryService;
        this.latestReadingService = latestReadingService;
    }

    @GetMapping
    public ResponseEntity<ReadingPageResponse> getReadings(

            @RequestParam(required = false) String area,
            @RequestParam(required = false) String metric,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Reading> result = readingQueryService.searchReadings(
                area, metric, from, to, page, size
        );

        if (result.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No readings found for the given filters"
            );
        }

        ReadingPageResponse response = new ReadingPageResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Reading>> getLatestReadings(
            @RequestParam String area) {

        List<Reading> readings =
                latestReadingService.getLatestReadings(area);

        if (readings.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No readings found for area: " + area
            );
        }

        return ResponseEntity.ok(readings);
    }
}
