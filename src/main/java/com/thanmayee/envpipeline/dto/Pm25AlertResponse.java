package com.thanmayee.envpipeline.dto;

import java.time.Instant;

public record Pm25AlertResponse(
        String area,
        String metric,
        double movingAverage,
        double threshold,
        int windowCount,
        Instant evaluatedAt,
        String dataBasis
) {
}
