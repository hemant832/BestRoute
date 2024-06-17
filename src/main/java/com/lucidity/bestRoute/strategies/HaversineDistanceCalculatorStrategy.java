package com.lucidity.bestRoute.strategies;

import com.lucidity.bestRoute.models.GeoLocation;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class HaversineDistanceCalculatorStrategy implements DistanceCalculatorStrategy{
    private static final double R = 6371; // Radius of the Earth in kilometers

    @Override
    public double calculateDistance(GeoLocation loc1, GeoLocation loc2) {
        double latDistance = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double lonDistance = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
