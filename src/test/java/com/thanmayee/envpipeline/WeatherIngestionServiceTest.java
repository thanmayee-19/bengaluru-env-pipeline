package com.thanmayee.envpipeline;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.service.WeatherIngestionService;
import com.thanmayee.envpipeline.source.DataSource;
import com.thanmayee.envpipeline.pipeline.Pipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class WeatherIngestionServiceTest {

    private DataSource dataSource;
    private ReadingRepository repository;
    private WeatherIngestionService service;
    private Pipeline pipeline;

    @BeforeEach
    void setUp() {
        dataSource = mock(DataSource.class);
        repository = mock(ReadingRepository.class);
        pipeline = mock(Pipeline.class);

        // Pass the list through the mocked pipeline unchanged.
        when(pipeline.process(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service = new WeatherIngestionService(
                List.of(dataSource),
                repository,
                pipeline
        );
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

        when(dataSource.fetch(Area.KORAMANGALA))
                .thenReturn(List.of(reading));

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

        when(dataSource.fetch(Area.KORAMANGALA))
                .thenReturn(List.of(reading));

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
        when(dataSource.fetch(any(Area.class)))
                .thenReturn(List.of());

        int inserted = service.ingest();

        assertEquals(0, inserted);
        verify(repository, never()).save(any(Reading.class));
    }
}