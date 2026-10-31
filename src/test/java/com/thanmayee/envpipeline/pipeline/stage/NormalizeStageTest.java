package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NormalizeStageTest {

    private final NormalizeStage stage = new NormalizeStage();

    private Reading createReading() {
        Reading reading = new Reading();
        reading.setArea(" koramangala ");
        reading.setMetric(" TEMPERATURE_2M ");
        reading.setValue(28.5);
        reading.setUnit(" C ");
        reading.setSource(" open-meteo-weather ");
        reading.setRecordedAt(Instant.now());
        return reading;
    }

    @Test
    void normalizesReadingFields() {
        Reading reading = createReading();

        List<Reading> result = stage.process(List.of(reading));

        assertEquals(1, result.size());
        assertEquals("KORAMANGALA", result.get(0).getArea());
        assertEquals("temperature_2m", result.get(0).getMetric());
        assertEquals("C", result.get(0).getUnit());
        assertEquals("open-meteo-weather", result.get(0).getSource());
    }

    @Test
    void handlesNullInput() {
        List<Reading> result = stage.process(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void skipsNullReading() {
        List<Reading> result = stage.process(
            java.util.Arrays.asList((Reading) null)
        );

        assertTrue(result.isEmpty());
    }
}
