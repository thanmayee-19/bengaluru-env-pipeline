package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import java.util.ArrayList;
import java.util.List;

public class ValidateStage implements PipelineStage {

    @Override
    public List<Reading> process(List<Reading> readings) {
        List<Reading> valid = new ArrayList<>();

        if (readings == null) {
            return valid;
        }

        for (Reading reading : readings) {
            if (reading == null
                    || reading.getArea() == null
                    || reading.getArea().isBlank()
                    || reading.getMetric() == null
                    || reading.getMetric().isBlank()
                    || reading.getValue() == null
                    || !Double.isFinite(reading.getValue())
                    || reading.getRecordedAt() == null) {
                continue;
            }

            double value = reading.getValue();
            String metric = reading.getMetric().trim().toLowerCase();

            if (metric.equals("pm2_5") && value < 0) {
                continue;
            }

            if (metric.equals("temperature_2m")
                    && (value < -90 || value > 65)) {
                continue;
            }

            valid.add(reading);
        }

        return valid;
    }
}
