package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrichStageTest {

    private final EnrichStage stage = new EnrichStage();

    private Reading createReading(String metric) {
        Reading reading = new Reading();
        reading.setArea("KORAMANGALA");
        reading.setMetric(metric);
        reading.setValue(28.5);
        reading.setRecordedAt(Instant.now());
        return reading;
    }

    @Test
    void addsDefaultPm25Unit() {
        Reading reading = createReading("pm2_5");

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("µg/m³", result.get(0).getUnit());
    }

    @Test
    void addsDefaultTemperatureUnit() {
        Reading reading = createReading("temperature_2m");

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("°C", result.get(0).getUnit());
    }

    @Test
    void addsUnknownSourceWhenMissing() {
        Reading reading = createReading("pm2_5");

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("unknown", result.get(0).getSource());
    }

    @Test
    void preservesExistingUnitAndSource() {
        Reading reading = createReading("pm2_5");
        reading.setUnit("custom-unit");
        reading.setSource("custom-source");

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("custom-unit", result.get(0).getUnit());
        assertEquals("custom-source", result.get(0).getSource());
    }

    @Test
    void handlesNullInput() {
        List<Reading> result = stage.process(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void assignsGoodCategory() {
        Reading reading = createReading("pm2_5");
        reading.setValue(22.5);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Good", result.get(0).getPm25Category());
    }

    @Test
    void assignsGoodCategoryAtUpperBoundary() {
        Reading reading = createReading("pm2_5");
        reading.setValue(30.0);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Good", result.get(0).getPm25Category());
    }

    @Test
    void assignsModerateCategory() {
        Reading reading = createReading("pm2_5");
        reading.setValue(33.9);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Moderate", result.get(0).getPm25Category());
    }

    @Test
    void assignsModerateCategoryAtUpperBoundary() {
        Reading reading = createReading("pm2_5");
        reading.setValue(60.0);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Moderate", result.get(0).getPm25Category());
    }

    @Test
    void assignsPoorCategory() {
        Reading reading = createReading("pm2_5");
        reading.setValue(75.0);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Poor", result.get(0).getPm25Category());
    }

    @Test
    void assignsPoorCategoryAtUpperBoundary() {
        Reading reading = createReading("pm2_5");
        reading.setValue(90.0);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Poor", result.get(0).getPm25Category());
    }

    @Test
    void assignsVeryPoorCategory() {
        Reading reading = createReading("pm2_5");
        reading.setValue(110.0);

        List<Reading> result = stage.process(List.of(reading));

        assertEquals("Very Poor", result.get(0).getPm25Category());
    }

    @Test
    void doesNotAssignPm25CategoryToTemperature() {
        Reading reading = createReading("temperature_2m");
        reading.setValue(22.3);

        List<Reading> result = stage.process(List.of(reading));

        assertNull(result.get(0).getPm25Category());
    }
}
