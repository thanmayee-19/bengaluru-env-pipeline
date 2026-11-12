package com.thanmayee.envpipeline.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.thanmayee.envpipeline.entity.Reading;

public interface ReadingRepository
        extends JpaRepository<Reading, Long>,
                JpaSpecificationExecutor<Reading> {
	
	List<Reading> findByAreaOrderByRecordedAtDesc(String area);

    boolean existsByAreaAndMetricAndRecordedAt(
            String area,
            String metric,
            Instant recordedAt
    );
}
