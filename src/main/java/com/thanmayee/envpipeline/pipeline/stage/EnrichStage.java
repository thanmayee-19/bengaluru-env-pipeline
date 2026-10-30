package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import java.util.ArrayList;
import java.util.List;

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

            if (reading.getSource() == null
                    || reading.getSource().isBlank()) {
                reading.setSource("unknown");
            }

            enriched.add(reading);
        }

        return enriched;
    }
}
