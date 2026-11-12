package com.thanmayee.envpipeline.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.exception.ResourceNotFoundException;
import com.thanmayee.envpipeline.repository.ReadingRepository;

@Service
public class CacheBenchmarkService {

    private final ReadingRepository readingRepository;
    private final LatestReadingService latestReadingService;

    public CacheBenchmarkService(
            ReadingRepository readingRepository,
            LatestReadingService latestReadingService) {

        this.readingRepository = readingRepository;
        this.latestReadingService = latestReadingService;
    }

    public Map<String, Object> benchmark(
            String area, int iterations) {

        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(
                    "Area parameter is required");
        }

        if (iterations < 1 || iterations > 10000) {
            throw new IllegalArgumentException(
                    "Iterations must be between 1 and 10000");
        }

        // Warm up database and cache
        List<Reading> uncachedResult = fetchUncached(area);
        List<Reading> cachedResult =
                latestReadingService.getLatestReadings(area);

        if (uncachedResult.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No readings found for area: " + area);
        }

        // Verify both approaches return the same readings
        boolean resultsMatch =
                getReadingIds(uncachedResult)
                .equals(getReadingIds(cachedResult));

        // Benchmark direct database retrieval
        long uncachedStart = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            fetchUncached(area);
        }

        long uncachedTime =
                System.nanoTime() - uncachedStart;

        // Benchmark cache retrieval
        long cachedStart = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            latestReadingService.getLatestReadings(area);
        }

        long cachedTime =
                System.nanoTime() - cachedStart;

        Map<String, Object> results = new LinkedHashMap<>();

        results.put("area", area);
        results.put("iterations", iterations);
        results.put("readingCount", cachedResult.size());
        results.put("resultsMatch", resultsMatch);

        results.put("uncachedTotalNanos", uncachedTime);
        results.put("cachedTotalNanos", cachedTime);

        results.put(
                "uncachedAverageNanos",
                uncachedTime / iterations);

        results.put(
                "cachedAverageNanos",
                cachedTime / iterations);

        results.put(
                "speedup",
                cachedTime == 0
                        ? null
                        : (double) uncachedTime / cachedTime);

        return results;
    }

    private List<Reading> fetchUncached(String area) {

        List<Reading> readings =
                readingRepository
                .findByAreaOrderByRecordedAtDesc(area);

        Map<String, Reading> latestByMetric =
                new LinkedHashMap<>();

        for (Reading reading : readings) {
            latestByMetric.putIfAbsent(
                    reading.getMetric(),
                    reading);
        }

        return List.copyOf(latestByMetric.values());
    }

    private List<Long> getReadingIds(List<Reading> readings) {

        return readings.stream()
                .map(Reading::getId)
                .toList();
    }
}
