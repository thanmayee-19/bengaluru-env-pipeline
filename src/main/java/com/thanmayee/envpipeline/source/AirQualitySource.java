package com.thanmayee.envpipeline.source;

import com.thanmayee.envpipeline.dto.AirQualityApiResponse;
import com.thanmayee.envpipeline.dto.AirQualityHourly;
import com.thanmayee.envpipeline.entity.Reading;
import com.thanmayee.envpipeline.model.Area;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class AirQualitySource implements DataSource {

    private static final String API_URL =
            "https://air-quality-api.open-meteo.com/v1/air-quality";

    private static final String TIMEZONE = "Asia/Kolkata";

    private final RestClient restClient;

    public AirQualitySource(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public String name() {
        return "open-meteo-air-quality";
    }

    @Override
    public List<Reading> fetch(Area area) {

        List<Reading> readings = new ArrayList<>();

        AirQualityApiResponse response = restClient.get()
                .uri(API_URL
                        + "?latitude=" + area.getLatitude()
                        + "&longitude=" + area.getLongitude()
                        + "&hourly=pm2_5"
                        + "&timezone=" + TIMEZONE
                        + "&forecast_days=1")
                .retrieve()
                .body(AirQualityApiResponse.class);

        if (response == null || response.getHourly() == null) {
            return readings;
        }

        AirQualityHourly hourly = response.getHourly();

        if (hourly.getTime() == null || hourly.getPm25() == null) {
            return readings;
        }

        int count = Math.min(
                hourly.getTime().size(),
                hourly.getPm25().size()
        );

        for (int i = 0; i < count; i++) {

            Double pm25 = hourly.getPm25().get(i);

            if (pm25 == null || !Double.isFinite(pm25)) {
                continue;
            }

            LocalDateTime localTime =
                    LocalDateTime.parse(hourly.getTime().get(i));

            Instant recordedAt = localTime
                    .atZone(ZoneId.of(TIMEZONE))
                    .toInstant();

            Reading reading = new Reading();
            reading.setArea(area.name());
            reading.setMetric("pm2_5");
            reading.setValue(pm25);
            reading.setUnit("µg/m³");
            reading.setSource(name());
            reading.setRecordedAt(recordedAt);

            readings.add(reading);
        }

        return readings;
    }
}
