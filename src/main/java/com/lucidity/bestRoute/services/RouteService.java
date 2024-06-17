package com.lucidity.bestRoute.services;

import com.lucidity.bestRoute.dto.ResponseStatus;
import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.models.*;
import com.lucidity.bestRoute.repositories.CustomerRepository;
import com.lucidity.bestRoute.repositories.DeliveryExecutiveRepository;
import com.lucidity.bestRoute.repositories.OrderRepository;
import com.lucidity.bestRoute.repositories.RestaurantRepository;
import com.lucidity.bestRoute.strategies.BestRouteCalculatorStrategy;
import com.lucidity.bestRoute.strategies.DistanceCalculatorStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {

    private final OrderRepository orderRepository;
    private final DeliveryExecutiveRepository deliveryExecutiveRepository;
    private final BestRouteCalculatorStrategy bestRouteCalculatorStrategy;

    public RouteService(OrderRepository orderRepository, DeliveryExecutiveRepository deliveryExecutiveRepository, BestRouteCalculatorStrategy bestRouteCalculatorStrategy) {
        this.orderRepository = orderRepository;
        this.deliveryExecutiveRepository = deliveryExecutiveRepository;
        this.bestRouteCalculatorStrategy = bestRouteCalculatorStrategy;
    }

    public ShortestRouteResponseDto findShortestDeliveryTime(long deliveryExecutiveId, List<Long> orderIds) {

        Optional<DeliveryExecutive> deliveryExecutiveOptional = deliveryExecutiveRepository.findById(deliveryExecutiveId);
        DeliveryExecutive deliveryExecutive = deliveryExecutiveOptional.get();
        GeoLocation start = deliveryExecutive.getGeoLocation();
        List<Order> orders = orderRepository.findAllById(orderIds);

        return bestRouteCalculatorStrategy.getBestRoute(start, orders);
    }
}
