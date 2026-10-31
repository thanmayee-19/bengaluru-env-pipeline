package com.thanmayee.envpipeline.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AirQualityHourly {

    private List<String> time;

    @JsonProperty("pm2_5")
    private List<Double> pm25;

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<Double> getPm25() {
        return pm25;
    }

    public void setPm25(List<Double> pm25) {
        this.pm25 = pm25;
    }
}
