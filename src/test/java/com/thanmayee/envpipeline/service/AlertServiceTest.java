package com.thanmayee.envpipeline.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.thanmayee.envpipeline.config.AlertProperties;
import com.thanmayee.envpipeline.dto.Pm25AlertResponse;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private ReadingRepository readingRepository;

    private AlertService alertService;

    private static final String AREA = "KORAMANGALA";
    private static final String METRIC = "pm2_5";

    @BeforeEach
    void setUp() {

        AlertProperties properties = new AlertProperties();
        properties.setPm25Threshold(35.0);
        properties.setWindowSize(3);

        alertService = new AlertService(
                readingRepository,
                properties
        );
    }

    private Reading createReading(Double value) {

        Reading reading = new Reading();
        reading.setArea(AREA);
        reading.setMetric(METRIC);
        reading.setValue(value);
        reading.setRecordedAt(Instant.now());

        return reading;
    }

    private void mockReadings(List<Reading> readings) {

        when(readingRepository.findDistinctAreaByMetric(METRIC))
                .thenReturn(List.of(AREA));

        when(readingRepository
                .findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
                        eq(AREA),
                        eq(METRIC),
                        any(Instant.class),
                        any(Pageable.class)
                ))
                .thenReturn(readings);
    }

    @Test
    void shouldReturnAlertWhenAverageIsAboveThreshold() {

        mockReadings(List.of(
                createReading(40.0),
                createReading(45.0),
                createReading(50.0)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertEquals(1, alerts.size());
        assertEquals(45.0, alerts.get(0).movingAverage(), 0.001);
        assertEquals(35.0, alerts.get(0).threshold());
        assertEquals(3, alerts.get(0).windowCount());
        assertEquals(AREA, alerts.get(0).area());
        assertEquals(METRIC, alerts.get(0).metric());
    }

    @Test
    void shouldNotReturnAlertWhenAverageIsBelowThreshold() {

        mockReadings(List.of(
                createReading(20.0),
                createReading(25.0),
                createReading(30.0)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertTrue(alerts.isEmpty());
    }

    @Test
    void shouldReturnAlertWhenAverageEqualsThreshold() {

        mockReadings(List.of(
                createReading(30.0),
                createReading(35.0),
                createReading(40.0)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertEquals(1, alerts.size());
        assertEquals(35.0, alerts.get(0).movingAverage(), 0.001);
    }

    @Test
    void shouldCalculateAverageWithFewerThanWindowSizeReadings() {

        mockReadings(List.of(
                createReading(40.0),
                createReading(30.0)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertEquals(1, alerts.size());
        assertEquals(35.0, alerts.get(0).movingAverage(), 0.001);
        assertEquals(2, alerts.get(0).windowCount());
    }

    @Test
    void shouldReturnNoAlertWhenThereAreNoReadings() {

        mockReadings(List.of());

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertTrue(alerts.isEmpty());
    }

    @Test
    void shouldIgnoreNullNegativeAndNonFiniteValues() {

        mockReadings(List.of(
                createReading(null),
                createReading(-10.0),
                createReading(Double.NaN),
                createReading(Double.POSITIVE_INFINITY)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertTrue(alerts.isEmpty());
    }

    @Test
    void shouldReturnNoAlertWhenNoAreasHaveReadings() {

        when(readingRepository.findDistinctAreaByMetric(METRIC))
                .thenReturn(List.of());

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertTrue(alerts.isEmpty());

        verify(readingRepository, never())
                .findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
                        anyString(),
                        anyString(),
                        any(Instant.class),
                        any(Pageable.class)
                );
    }
    
    @Test
    void shouldEvaluateMultipleAreasIndependently() {

        String secondArea = "INDIRANAGAR";

        when(readingRepository.findDistinctAreaByMetric(METRIC))
                .thenReturn(List.of(AREA, secondArea));

        when(readingRepository
                .findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
                        eq(AREA),
                        eq(METRIC),
                        any(Instant.class),
                        any(Pageable.class)
                ))
                .thenReturn(List.of(
                        createReading(40.0),
                        createReading(45.0),
                        createReading(50.0)
                ));

        Reading secondAreaReading = new Reading();
        secondAreaReading.setArea(secondArea);
        secondAreaReading.setMetric(METRIC);
        secondAreaReading.setValue(20.0);
        secondAreaReading.setRecordedAt(Instant.now());

        when(readingRepository
                .findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
                        eq(secondArea),
                        eq(METRIC),
                        any(Instant.class),
                        any(Pageable.class)
                ))
                .thenReturn(List.of(secondAreaReading));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertEquals(1, alerts.size());
        assertEquals(AREA, alerts.get(0).area());
    }

    @Test
    void shouldIncludeEvaluationTimeAndDataBasis() {

        mockReadings(List.of(
                createReading(40.0),
                createReading(45.0),
                createReading(50.0)
        ));

        List<Pm25AlertResponse> alerts = alertService.getAlerts();

        assertNotNull(alerts.get(0).evaluatedAt());
        assertTrue(alerts.get(0).dataBasis().contains("Forecast-derived"));
    }
}
