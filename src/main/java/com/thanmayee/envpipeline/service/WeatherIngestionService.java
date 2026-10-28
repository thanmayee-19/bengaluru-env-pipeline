package com.thanmayee.envpipeline.service;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.source.DataSource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeatherIngestionService {

    private final DataSource dataSource;
    private final ReadingRepository repository;

    public WeatherIngestionService(
            DataSource dataSource,
            ReadingRepository repository) {
        this.dataSource = dataSource;
        this.repository = repository;
    }

    @Transactional
    public int ingest() {
        List<Reading> readings = dataSource.fetchReadings();
        int inserted = 0;

        for (Reading reading : readings) {
            boolean exists = repository
                .existsByAreaAndMetricAndRecordedAt(
                    reading.getArea(),
                    reading.getMetric(),
                    reading.getRecordedAt()
                );

            if (!exists) {
                repository.save(reading);
                inserted++;
            }
        }

        return inserted;
    }
}
