package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NormalizeStage implements PipelineStage {

    @Override
    public List<Reading> process(List<Reading> readings) {
        List<Reading> normalized = new ArrayList<>();

        if (readings == null) {
            return normalized;
        }

        for (Reading reading : readings) {
            if (reading == null) {
                continue;
            }

            if (reading.getArea() != null) {
                reading.setArea(reading.getArea().trim());
            }

            if (reading.getMetric() != null) {
                reading.setMetric(
                    reading.getMetric().trim().toLowerCase(Locale.ROOT)
                );
            }

            if (reading.getUnit() != null) {
                reading.setUnit(reading.getUnit().trim());
            }

            if (reading.getSource() != null) {
                reading.setSource(reading.getSource().trim());
            }

            normalized.add(reading);
        }

        return normalized;
    }
}
