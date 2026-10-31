package com.thanmayee.envpipeline.config;

import com.thanmayee.envpipeline.pipeline.Pipeline;
import com.thanmayee.envpipeline.pipeline.stage.EnrichStage;
import com.thanmayee.envpipeline.pipeline.stage.NormalizeStage;
import com.thanmayee.envpipeline.pipeline.stage.ValidateStage;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {

    @Bean
    public Pipeline readingPipeline() {
        return new Pipeline(List.of(
            new ValidateStage(),
            new NormalizeStage(),
            new EnrichStage()
        ));
    }
}
