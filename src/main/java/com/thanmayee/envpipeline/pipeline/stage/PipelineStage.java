package com.thanmayee.envpipeline.pipeline.stage;

import com.thanmayee.envpipeline.entity.Reading;
import java.util.List;

public interface PipelineStage {
    List<Reading> process(List<Reading> readings);
}
