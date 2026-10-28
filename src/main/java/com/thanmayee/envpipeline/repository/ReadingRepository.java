
package com.thanmayee.envpipeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thanmayee.envpipeline.entity.Reading;

import java.time.Instant;

public interface ReadingRepository
        extends JpaRepository<Reading, Long> {

    boolean existsByAreaAndMetricAndRecordedAt(
        String area,
        String metric,
        Instant recordedAt
    );
}
