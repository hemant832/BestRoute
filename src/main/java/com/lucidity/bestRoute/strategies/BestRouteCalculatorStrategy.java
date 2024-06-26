package com.lucidity.bestRoute.strategies;

import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.exceptions.RestaurantNotFoundException;
import com.lucidity.bestRoute.models.GeoLocation;
import com.lucidity.bestRoute.models.Order;

import java.util.List;

public interface BestRouteCalculatorStrategy {
    ShortestRouteResponseDto getBestRoute(GeoLocation start, List<Order> orders) throws RestaurantNotFoundException;

    double calculateTime(GeoLocation start, GeoLocation end);
}
