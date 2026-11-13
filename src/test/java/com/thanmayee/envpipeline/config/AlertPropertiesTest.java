package com.thanmayee.envpipeline.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertPropertiesTest {

    @Test
    void shouldAcceptValidConfiguration() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(35.0);
        properties.setWindowSize(3);

        assertDoesNotThrow(properties::afterPropertiesSet);
    }

    @Test
    void shouldRejectZeroThreshold() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(0);
        properties.setWindowSize(3);

        assertThrows(
                IllegalArgumentException.class,
                properties::afterPropertiesSet
        );
    }

    @Test
    void shouldRejectNegativeThreshold() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(-10);
        properties.setWindowSize(3);

        assertThrows(
                IllegalArgumentException.class,
                properties::afterPropertiesSet
        );
    }

    @Test
    void shouldRejectNonFiniteThreshold() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(Double.NaN);
        properties.setWindowSize(3);

        assertThrows(
                IllegalArgumentException.class,
                properties::afterPropertiesSet
        );
    }

    @Test
    void shouldRejectZeroWindowSize() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(35.0);
        properties.setWindowSize(0);

        assertThrows(
                IllegalArgumentException.class,
                properties::afterPropertiesSet
        );
    }

    @Test
    void shouldRejectNegativeWindowSize() {

        AlertProperties properties = new AlertProperties();

        properties.setPm25Threshold(35.0);
        properties.setWindowSize(-1);

        assertThrows(
                IllegalArgumentException.class,
                properties::afterPropertiesSet
        );
    }
}
