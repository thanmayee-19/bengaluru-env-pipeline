package com.thanmayee.envpipeline.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.repository.ReadingRepository;
import com.thanmayee.envpipeline.repository.ReadingSpecifications;

@Service
public class ReadingQueryService {

    private final ReadingRepository readingRepository;

    public ReadingQueryService(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    public Page<Reading> searchReadings(
            String area,
            String metric,
            Instant from,
            Instant to,
            int page,
            int size) {

        // Validate pagination
        if (page < 0) {
            throw new IllegalArgumentException(
                "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
            );
        }

        // Validate time range
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                "From timestamp cannot be later than to timestamp"
            );
        }

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(
                Sort.Order.desc("recordedAt"),
                Sort.Order.desc("id")
            )
        );

        return readingRepository.findAll(
            ReadingSpecifications.filterReadings(
                area,
                metric,
                from,
                to
            ),
            pageable
        );
    }
}
