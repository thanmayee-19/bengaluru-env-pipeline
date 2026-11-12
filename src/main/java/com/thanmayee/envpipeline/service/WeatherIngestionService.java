package com.thanmayee.envpipeline.service;

import com.thanmayee.envpipeline.cache.LRUCache;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;
import com.thanmayee.envpipeline.pipeline.Pipeline;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.source.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class WeatherIngestionService {

    private static final Logger logger =
            LoggerFactory.getLogger(WeatherIngestionService.class);

    private final List<DataSource> dataSources;
    private final ReadingRepository repository;
    private final Pipeline pipeline;
    private final ExecutorService ingestionExecutor;
    private final RetryExecutor retryExecutor;
    private final LRUCache<String, List<Reading>> latestReadingCache;

    public WeatherIngestionService(
            List<DataSource> dataSources,
            ReadingRepository repository,
            Pipeline pipeline,
            @Qualifier("ingestionExecutor") ExecutorService ingestionExecutor,
            RetryExecutor retryExecutor,
            LRUCache<String, List<Reading>> latestReadingCache) {

        this.dataSources = dataSources;
        this.repository = repository;
        this.pipeline = pipeline;
        this.ingestionExecutor = ingestionExecutor;
        this.retryExecutor = retryExecutor;
        this.latestReadingCache = latestReadingCache;
    }

    public int ingest() {

        List<CompletableFuture<List<Reading>>> futures =
                new ArrayList<>();

        for (DataSource dataSource : dataSources) {
            for (Area area : Area.values()) {

                CompletableFuture<List<Reading>> future =
                        CompletableFuture.supplyAsync(() -> {

                            long startTime = System.nanoTime();

                            try {
                                logger.info(
                                        "Started ingestion: source={}, area={}",
                                        dataSource.name(), area.name()
                                );

                                List<Reading> readings =
                                        retryExecutor.execute(
                                                () -> dataSource.fetch(area),
                                                dataSource.name(),
                                                area.name()
                                        );

                                long elapsedTime =
                                        (System.nanoTime() - startTime)
                                                / 1_000_000;

                                int count = readings == null
                                        ? 0 : readings.size();

                                logger.info(
                                        "Ingestion successful: source={}, area={}, readings={}, elapsed={} ms",
                                        dataSource.name(),
                                        area.name(),
                                        count,
                                        elapsedTime
                                );

                                return readings == null
                                        ? List.<Reading>of()
                                        : readings;

                            } catch (Exception e) {

                                long elapsedTime =
                                        (System.nanoTime() - startTime)
                                                / 1_000_000;

                                logger.error(
                                        "Ingestion failed: source={}, area={}, elapsed={} ms, reason={}",
                                        dataSource.name(),
                                        area.name(),
                                        elapsedTime,
                                        e.getMessage()
                                );

                                return List.<Reading>of();
                            }

                        }, ingestionExecutor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<Reading> allReadings = new ArrayList<>();

        for (CompletableFuture<List<Reading>> future : futures) {
            allReadings.addAll(future.join());
        }

        List<Reading> results = pipeline.process(allReadings);

        if (results == null || results.isEmpty()) {
            logger.info("No valid readings available for saving.");
            return 0;
        }

        int inserted = 0;

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

        // Clear cached latest readings only when new data is inserted.
        if (inserted > 0) {
            latestReadingCache.clear();
            logger.info("Latest readings cache cleared after ingestion.");
        }

        logger.info(
                "Concurrent ingestion completed. New readings inserted: {}",
                inserted
        );

        return inserted;
    }
}
