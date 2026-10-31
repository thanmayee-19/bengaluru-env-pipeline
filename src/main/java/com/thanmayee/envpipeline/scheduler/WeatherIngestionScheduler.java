package com.thanmayee.envpipeline.scheduler;

import com.thanmayee.envpipeline.service.WeatherIngestionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherIngestionScheduler {

    private final WeatherIngestionService ingestionService;

    public WeatherIngestionScheduler(
            WeatherIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Scheduled(fixedDelayString = "${app.ingestion.interval-ms:3600000}")
    public void scheduledIngestion() {
        int inserted = ingestionService.ingest();

        System.out.println(
                "Scheduled ingestion completed. New readings inserted: "
                        + inserted
        );
    }
}