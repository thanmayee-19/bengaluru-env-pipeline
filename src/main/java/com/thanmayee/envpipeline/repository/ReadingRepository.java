package com.thanmayee.envpipeline.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("SELECT DISTINCT r.area FROM Reading r WHERE r.metric = :metric")
    List<String> findDistinctAreaByMetric(@Param("metric") String metric);

    List<Reading> findByAreaAndMetricAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
            String area,
            String metric,
            Instant evaluatedAt,
            Pageable pageable
    );
}
