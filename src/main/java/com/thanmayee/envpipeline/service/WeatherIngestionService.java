package com.thanmayee.envpipeline.service;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;
import com.thanmayee.envpipeline.pipeline.Pipeline;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.source.DataSource;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherIngestionService {

    private final List<DataSource> dataSources;
    private final ReadingRepository repository;
    private final Pipeline pipeline;

    public WeatherIngestionService(
            List<DataSource> dataSources,
            ReadingRepository repository,
            Pipeline pipeline) {

        this.dataSources = dataSources;
        this.repository = repository;
        this.pipeline = pipeline;
    }

    public int ingest() {
        int inserted = 0;

        for (DataSource dataSource : dataSources) {
            for (Area area : Area.values()) {

                try {
                    List<Reading> readings = dataSource.fetch(area);

                    if (readings == null || readings.isEmpty()) {
                        continue;
                    }

                    List<Reading> results = pipeline.process(readings);

                    if (results == null || results.isEmpty()) {
                        continue;
                    }

                    for (Reading result : results) {
                        if (result == null) {
                            continue;
                        }

                        boolean exists =
                                repository.existsByAreaAndMetricAndRecordedAt(
                                        result.getArea(),
                                        result.getMetric(),
                                        result.getRecordedAt()
                                );

                        if (!exists) {
                            repository.save(result);
                            inserted++;
                        }
                    }

                } catch (Exception e) {
                    System.err.println(
                            "Ingestion failed for source "
                                    + dataSource.name()
                                    + ", area "
                                    + area.name()
                                    + ": "
                                    + e.getMessage()
                    );
                }
            }
        }

        return inserted;
    }
}
