package com.lucidity.bestRoute.controllers;


import com.lucidity.bestRoute.dto.ResponseStatus;
import com.lucidity.bestRoute.dto.ShortestRouteRequestDto;
import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.exceptions.RestaurantNotFindException;
import com.lucidity.bestRoute.services.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@ControllerAdvice
public class RouteController {

    private RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/shortest-route")
    public ShortestRouteResponseDto getShortestRoute(@RequestBody ShortestRouteRequestDto request) throws RestaurantNotFindException {
        long deliveryExecutiveId = request.getDeliveryExecutiveId();
        List<Long> orderIds = request.getOrderIds();
        ShortestRouteResponseDto response = routeService.findShortestDeliveryTime(deliveryExecutiveId, orderIds);
        return response;
    }

    @ExceptionHandler(RestaurantNotFindException.class)
    public void handleRestaurantNotFoundException(){

    }
}
