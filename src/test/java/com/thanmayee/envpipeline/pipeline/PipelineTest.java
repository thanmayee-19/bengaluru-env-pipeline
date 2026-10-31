package com.thanmayee.envpipeline.pipeline;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.pipeline.stage.NormalizeStage;
import com.thanmayee.envpipeline.pipeline.stage.ValidateStage;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    @Test
    void normalizesAndValidatesReading() {
        Reading reading = new Reading();
        reading.setArea(" KORAMANGALA ");
        reading.setMetric(" TEMPERATURE_2M ");
        reading.setValue(28.5);
        reading.setUnit(" C ");
        reading.setRecordedAt(Instant.now());
        reading.setSource("open-meteo-weather");

        Pipeline pipeline = new Pipeline(List.of(
                new ValidateStage(),
                new NormalizeStage()
        ));

        List<Reading> result = pipeline.process(List.of(reading));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("KORAMANGALA", result.get(0).getArea());
        assertEquals("temperature_2m", result.get(0).getMetric());
        assertEquals("C", result.get(0).getUnit());
        assertEquals("open-meteo-weather", result.get(0).getSource());
        assertEquals(28.5, result.get(0).getValue());
    }
}