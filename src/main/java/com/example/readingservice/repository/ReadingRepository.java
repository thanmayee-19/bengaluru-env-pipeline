
package com.example.readingservice.repository;

import com.example.readingservice.entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;

public interface ReadingRepository
        extends JpaRepository<Reading, Long> {

    boolean existsByAreaAndMetricAndRecordedAt(
        String area,
        String metric,
        Instant recordedAt
    );
}
