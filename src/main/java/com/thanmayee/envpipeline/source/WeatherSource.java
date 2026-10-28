package com.thanmayee.envpipeline.source;

import com.thanmayee.envpipeline.dto.Hourly;
import com.thanmayee.envpipeline.dto.WeatherApiResponse;
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
public class WeatherSource implements DataSource {

    private static final String API_URL =
        "https://api.open-meteo.com/v1/forecast";

    private static final String TIMEZONE = "Asia/Kolkata";

    private final RestClient restClient;

    public WeatherSource(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public List<Reading> fetchReadings() {
        List<Reading> readings = new ArrayList<>();

        for (Area area : Area.values()) {
            WeatherApiResponse response = restClient.get()
                .uri(API_URL + "?latitude=" + area.getLatitude()
                    + "&longitude=" + area.getLongitude()
                    + "&hourly=temperature_2m"
                    + "&timezone=" + TIMEZONE)
                .retrieve()
                .body(WeatherApiResponse.class);

            if (response == null || response.getHourly() == null) {
                continue;
            }

            Hourly hourly = response.getHourly();

            if (hourly.getTime() == null
                    || hourly.getTemperature2m() == null) {
                continue;
            }

            int count = Math.min(
                hourly.getTime().size(),
                hourly.getTemperature2m().size()
            );

            for (int i = 0; i < count; i++) {
                Double temperature = hourly.getTemperature2m().get(i);

                if (temperature == null) {
                    continue;
                }

                LocalDateTime localTime =
                    LocalDateTime.parse(hourly.getTime().get(i));

                Instant observedAt = localTime
                    .atZone(ZoneId.of(TIMEZONE))
                    .toInstant();

                Reading reading = new Reading();
                reading.setArea(area.name());
                reading.setMetric("temperature_2m");
                reading.setValue(temperature);
                reading.setUnit("C");
                reading.setSource("open-meteo-weather");
                reading.setRecordedAt(observedAt);

                readings.add(reading);
            }
        }

        return readings;
    }
}
