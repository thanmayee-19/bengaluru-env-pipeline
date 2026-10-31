package com.thanmayee.envpipeline.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirQualityApiResponse {

    private AirQualityHourly hourly;

    public AirQualityHourly getHourly() {
        return hourly;
    }

    public void setHourly(AirQualityHourly hourly) {
        this.hourly = hourly;
    }
}
