package com.thanmayee.envpipeline.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;
import com.thanmayee.envpipeline.source.DataSource;

@Service
public class IngestionBenchmarkService {

    private static final Logger logger =
            LoggerFactory.getLogger(IngestionBenchmarkService.class);

    private final List<DataSource> dataSources;
    private final ExecutorService ingestionExecutor;

    public IngestionBenchmarkService(
            List<DataSource> dataSources,
            @Qualifier("ingestionExecutor")
            ExecutorService ingestionExecutor) {

        this.dataSources = dataSources;
        this.ingestionExecutor = ingestionExecutor;
    }

    public Map<String, Object> benchmark() {

        // 1. Sequential benchmark
        long sequentialStart = System.nanoTime();

        FetchResult sequentialResult = runSequentially();

        long sequentialTime =
                (System.nanoTime() - sequentialStart) / 1_000_000;

        // 2. Concurrent benchmark
        long concurrentStart = System.nanoTime();

        FetchResult concurrentResult = runConcurrently();

        long concurrentTime =
                (System.nanoTime() - concurrentStart) / 1_000_000;

        // 3. Log results
        logger.info("========== BENCHMARK RESULTS ==========");
        logger.info("Sequential time: {} ms", sequentialTime);
        logger.info("Concurrent time: {} ms", concurrentTime);
        logger.info("Sequential readings: {}",
                sequentialResult.readingCount());
        logger.info("Concurrent readings: {}",
                concurrentResult.readingCount());
        logger.info("=======================================");

        // 4. Return results
        Map<String, Object> results = new LinkedHashMap<>();

        results.put("sequentialTimeMs", sequentialTime);
        results.put("concurrentTimeMs", concurrentTime);

        results.put("sequentialSuccessfulTasks",
                sequentialResult.successfulTasks());
        results.put("concurrentSuccessfulTasks",
                concurrentResult.successfulTasks());

        results.put("sequentialReadingCount",
                sequentialResult.readingCount());
        results.put("concurrentReadingCount",
                concurrentResult.readingCount());

        return results;
    }

    private FetchResult runSequentially() {

        int successfulTasks = 0;
        int readingCount = 0;

        for (DataSource source : dataSources) {
            for (Area area : Area.values()) {

                FetchResult result = fetchOne(source, area);

                successfulTasks += result.successfulTasks();
                readingCount += result.readingCount();
            }
        }

        return new FetchResult(successfulTasks, readingCount);
    }

    private FetchResult runConcurrently() {

        List<CompletableFuture<FetchResult>> futures =
                new ArrayList<>();

        for (DataSource source : dataSources) {
            for (Area area : Area.values()) {

                CompletableFuture<FetchResult> future =
                        CompletableFuture.supplyAsync(
                                () -> fetchOne(source, area),
                                ingestionExecutor
                        );

                futures.add(future);
            }
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        int successfulTasks = 0;
        int readingCount = 0;

        for (CompletableFuture<FetchResult> future : futures) {

            FetchResult result = future.join();

            successfulTasks += result.successfulTasks();
            readingCount += result.readingCount();
        }

        return new FetchResult(successfulTasks, readingCount);
    }

    private FetchResult fetchOne(DataSource source, Area area) {

        try {

            List<Reading> readings = source.fetch(area);

            int count = readings == null ? 0 : readings.size();

            return new FetchResult(1, count);

        } catch (Exception e) {

            logger.error(
                    "Benchmark fetch failed: source={}, area={}, reason={}",
                    source.name(),
                    area.name(),
                    e.getMessage()
            );

            return new FetchResult(0, 0);
        }
    }

    private record FetchResult(
            int successfulTasks,
            int readingCount) {
    }
}
