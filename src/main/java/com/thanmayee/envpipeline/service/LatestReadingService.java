package com.thanmayee.envpipeline.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.thanmayee.envpipeline.cache.LRUCache;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;

@Service
public class LatestReadingService {

    private final ReadingRepository readingRepository;
    private final LRUCache<String, List<Reading>> latestReadingCache;

    public LatestReadingService(
            ReadingRepository readingRepository,
            LRUCache<String, List<Reading>> latestReadingCache) {

        this.readingRepository = readingRepository;
        this.latestReadingCache = latestReadingCache;
    }

    public List<Reading> getLatestReadings(String area) {

        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException(
                "Area parameter is required"
            );
        }

        // Check cache first
        List<Reading> cachedReadings = latestReadingCache.get(area);

        if (cachedReadings != null) {
            return cachedReadings;
        }

        // Cache miss: retrieve readings from database
        List<Reading> readings =
                readingRepository.findByAreaOrderByRecordedAtDesc(area);

        Map<String, Reading> latestByMetric = new LinkedHashMap<>();

        for (Reading reading : readings) {
            latestByMetric.putIfAbsent(
                reading.getMetric(),
                reading
            );
        }

        List<Reading> latestReadings =
                List.copyOf(latestByMetric.values());

        // Store the result in cache
        latestReadingCache.put(area, latestReadings);

        return latestReadings;
    }
}
