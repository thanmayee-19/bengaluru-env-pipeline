package com.thanmayee.envpipeline.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Hourly {

    private List<String> time;

    @JsonProperty("temperature_2m")
    private List<Double> temperature2m;

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<Double> getTemperature2m() {
        return temperature2m;
    }

    public void setTemperature2m(List<Double> temperature2m) {
        this.temperature2m = temperature2m;
    }
}
