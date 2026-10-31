package com.thanmayee.envpipeline.pipeline;

import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.pipeline.stage.PipelineStage;
import java.util.ArrayList;
import java.util.List;

public class Pipeline {

    private final List<PipelineStage> stages;

    public Pipeline(List<PipelineStage> stages) {
        this.stages = List.copyOf(stages);
    }

    public List<Reading> process(List<Reading> readings) {
        List<Reading> current = readings == null
                ? new ArrayList<>()
                : new ArrayList<>(readings);

        for (PipelineStage stage : stages) {
            current = stage.process(current);
        }

        return current;
    }
}
