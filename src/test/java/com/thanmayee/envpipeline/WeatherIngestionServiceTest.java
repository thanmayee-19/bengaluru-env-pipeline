package com.thanmayee.envpipeline;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.service.WeatherIngestionService;
import com.thanmayee.envpipeline.source.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class WeatherIngestionServiceTest {

    private DataSource dataSource;
    private ReadingRepository repository;
    private WeatherIngestionService service;

    @BeforeEach
    void setUp() {
        dataSource = mock(DataSource.class);
        repository = mock(ReadingRepository.class);
        service = new WeatherIngestionService(dataSource, repository);
    }

    private Reading createReading() {
        Reading reading = new Reading();
        reading.setArea("KORAMANGALA");
        reading.setMetric("temperature_2m");
        reading.setValue(28.5);
        reading.setUnit("C");
        reading.setSource("open-meteo-weather");
        reading.setRecordedAt(Instant.parse("2026-10-28T10:00:00Z"));
        return reading;
    }

    @Test
    void insertsNewReading() {
        Reading reading = createReading();

        when(dataSource.fetchReadings()).thenReturn(List.of(reading));
        when(repository.existsByAreaAndMetricAndRecordedAt(
                reading.getArea(),
                reading.getMetric(),
                reading.getRecordedAt()
        )).thenReturn(false);

        int inserted = service.ingest();

        assertEquals(1, inserted);
        verify(repository).save(reading);
    }

    @Test
    void skipsDuplicateReading() {
        Reading reading = createReading();

        when(dataSource.fetchReadings()).thenReturn(List.of(reading));
        when(repository.existsByAreaAndMetricAndRecordedAt(
                reading.getArea(),
                reading.getMetric(),
                reading.getRecordedAt()
        )).thenReturn(true);

        int inserted = service.ingest();

        assertEquals(0, inserted);
        verify(repository, never()).save(any(Reading.class));
    }

    @Test
    void handlesEmptyReadings() {
        when(dataSource.fetchReadings()).thenReturn(List.of());

        int inserted = service.ingest();

        assertEquals(0, inserted);
        verify(repository, never()).save(any(Reading.class));
    }
}
