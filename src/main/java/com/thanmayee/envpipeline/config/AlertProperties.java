package com.thanmayee.envpipeline.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.alert")
public class AlertProperties implements InitializingBean {

    private double pm25Threshold;
    private int windowSize;

    public double getPm25Threshold() {
        return pm25Threshold;
    }

    public void setPm25Threshold(double pm25Threshold) {
        this.pm25Threshold = pm25Threshold;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public void afterPropertiesSet() {

        if (!Double.isFinite(pm25Threshold)
                || pm25Threshold <= 0) {

            throw new IllegalArgumentException(
                    "PM2.5 threshold must be a finite positive number");
        }

        if (windowSize <= 0) {

            throw new IllegalArgumentException(
                    "Alert window size must be greater than zero");
        }
    }
}
