package com.thanmayee.envpipeline.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.thanmayee.envpipeline.entity.Reading;

import jakarta.persistence.criteria.Predicate;

public class ReadingSpecifications {

    public static Specification<Reading> filterReadings(
            String area,
            String metric,
            Instant from,
            Instant to) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (area != null && !area.isBlank()) {
                predicates.add(
                    criteriaBuilder.equal(root.get("area"), area)
                );
            }

            if (metric != null && !metric.isBlank()) {
                predicates.add(
                    criteriaBuilder.equal(root.get("metric"), metric)
                );
            }

            if (from != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("recordedAt"), from
                    )
                );
            }

            if (to != null) {
                predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get("recordedAt"), to
                    )
                );
            }

            return criteriaBuilder.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}
