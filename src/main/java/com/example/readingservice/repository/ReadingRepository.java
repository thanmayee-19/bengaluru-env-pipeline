package com.example.readingservice.repository;

import com.example.readingservice.entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRepository
        extends JpaRepository<Reading, Long> {
}