package io.github.dengliming.redismodule.redisgraph.model;

import java.util.List;

public class Point {

    private static final double EPSILON = 1e-5;

    private final double latitude;
    private final double longitude;

    /**
     * @param latitude
     * @param longitude
     */
    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point(List<Double> values) {
        if (values == null || values.size() != 2) {
            throw new IllegalArgumentException("Point requires two doubles.");
        }
        this.latitude = values.get(0);
        this.longitude = values.get(1);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
