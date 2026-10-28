
package com.example.readingservice.model;

public enum Area {

    KORAMANGALA(12.9352, 77.6245),
    INDIRANAGAR(12.9784, 77.6408),
    WHITEFIELD(12.9698, 77.7500),
    JAYANAGAR(12.9250, 77.5938),
    ELECTRONIC_CITY(12.8452, 77.6602),
    HEBBAL(13.0358, 77.5970);

    private final double latitude;
    private final double longitude;

    Area(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
