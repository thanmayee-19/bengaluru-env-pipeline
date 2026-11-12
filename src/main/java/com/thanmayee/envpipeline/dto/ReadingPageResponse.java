package com.thanmayee.envpipeline.dto;

import java.util.List;

import com.thanmayee.envpipeline.entity.Reading;

public record ReadingPageResponse(
        List<Reading> readings,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}