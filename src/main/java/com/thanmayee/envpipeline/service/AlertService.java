package com.thanmayee.envpipeline.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.thanmayee.envpipeline.config.AlertProperties;
import com.thanmayee.envpipeline.dto.Pm25AlertResponse;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;

@Service
public class AlertService {

    private static final String PM25_METRIC = "pm2_5";

    private static final String DATA_BASIS =
            "Forecast-derived estimate; not a confirmed current measurement.";

    private final ReadingRepository readingRepository;
    private final AlertProperties alertProperties;

    public AlertService(
            ReadingRepository readingRepository,
            AlertProperties alertProperties) {

        this.readingRepository = readingRepository;
        this.alertProperties = alertProperties;
    }

    public List<Pm25AlertResponse> getAlerts() {

        Instant evaluatedAt = Instant.now();

        double threshold = alertProperties.getPm25Threshold();
        int windowSize = alertProperties.getWindowSize();

        List<String> areas =
                readingRepository.findDistinctAreaByMetric(PM25_METRIC);

        List<Pm25AlertResponse> alerts = new ArrayList<>();

        for (String area : areas) {

            if (area == null || area.isBlank()) {
                continue;
            }

            List<Reading> readings =
                    readingRepository
                            .findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
                                    area,
                                    PM25_METRIC,
                                    evaluatedAt,
                                    PageRequest.of(0, windowSize)
                            );

            double movingAverage = 0.0;
            int validReadingCount = 0;

            for (Reading reading : readings) {

                if (reading == null || reading.getValue() == null) {
                    continue;
                }

                double value = reading.getValue();

                if (!Double.isFinite(value) || value < 0) {
                    continue;
                }

                validReadingCount++;

                movingAverage +=
                        (value - movingAverage) / validReadingCount;
            }

            // No valid readings means no alert.
            if (validReadingCount == 0) {
                continue;
            }

            // Boundary rule: moving average >= threshold.
            if (movingAverage >= threshold) {

                alerts.add(
                        new Pm25AlertResponse(
                                area,
                                PM25_METRIC,
                                movingAverage,
                                threshold,
                                validReadingCount,
                                evaluatedAt,
                                DATA_BASIS
                        )
                );
            }
        }

        return alerts;
    }
}