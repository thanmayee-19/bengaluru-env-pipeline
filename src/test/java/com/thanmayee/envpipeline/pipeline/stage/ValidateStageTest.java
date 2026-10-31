package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidateStageTest {

    private final ValidateStage stage = new ValidateStage();

    private Reading createReading(String metric, double value) {
        Reading reading = new Reading();
        reading.setArea("KORAMANGALA");
        reading.setMetric(metric);
        reading.setValue(value);
        reading.setUnit("unit");
        reading.setSource("test");
        reading.setRecordedAt(Instant.now());
        return reading;
    }

    @Test
    void acceptsValidReading() {
        Reading reading = createReading("pm2_5", 32.8);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals(1, result.size());
    }

    @Test
    void rejectsNegativePm25() {
        Reading reading = createReading("pm2_5", -5);

        List<Reading> result = stage.process(List.of(reading));

        assertTrue(result.isEmpty());
    }

    @Test
    void rejectsUnrealisticTemperature() {
        Reading reading = createReading("temperature_2m", 100);

        List<Reading> result = stage.process(List.of(reading));

        assertTrue(result.isEmpty());
    }

    @Test
    void rejectsNullReadings() {
        List<Reading> result = stage.process(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void rejectsInfiniteValues() {
        Reading reading = createReading("pm2_5", Double.POSITIVE_INFINITY);

        List<Reading> result = stage.process(List.of(reading));

        assertTrue(result.isEmpty());
    }
}
