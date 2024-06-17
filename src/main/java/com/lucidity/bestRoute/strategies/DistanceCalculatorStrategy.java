package com.lucidity.bestRoute.strategies;

import com.lucidity.bestRoute.models.GeoLocation;

public interface DistanceCalculatorStrategy {
    double calculateDistance(GeoLocation start, GeoLocation end);
}
