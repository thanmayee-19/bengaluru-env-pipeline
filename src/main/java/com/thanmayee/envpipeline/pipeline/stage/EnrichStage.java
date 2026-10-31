package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnrichStage implements PipelineStage {

    @Override
    public List<Reading> process(List<Reading> readings) {

        List<Reading> enriched = new ArrayList<>();

        if (readings == null) {
            return enriched;
        }

        for (Reading reading : readings) {

            if (reading == null) {
                continue;
            }

            if (reading.getSource() == null ||
                    reading.getSource().isBlank()) {
                reading.setSource("unknown");
            }

            if (reading.getUnit() == null ||
                    reading.getUnit().isBlank()) {

                if ("pm2_5".equals(reading.getMetric())) {
                    reading.setUnit("µg/m³");
                } else if ("temperature_2m".equals(reading.getMetric())) {
                    reading.setUnit("°C");
                }
            }

            if ("pm2_5".equals(reading.getMetric())) {
                reading.setPm25Category(getPm25Category(reading.getValue()));
            }

            enriched.add(reading);
        }

        return enriched;
    }

    private String getPm25Category(Double value) {

        if (value == null || value < 0) {
            return null;
        }

        if (value <= 30) {
            return "Good";
        } else if (value <= 60) {
            return "Moderate";
        } else if (value <= 90) {
            return "Poor";
        } else {
            return "Very Poor";
        }
    }
}