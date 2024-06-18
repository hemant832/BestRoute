package com.lucidity.bestRoute.strategies;

import com.lucidity.bestRoute.dto.ResponseStatus;
import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.exceptions.RestaurantNotFoundException;
import com.lucidity.bestRoute.models.GeoLocation;
import com.lucidity.bestRoute.models.Node;
import com.lucidity.bestRoute.models.Order;
import com.lucidity.bestRoute.models.Restaurant;
import com.lucidity.bestRoute.repositories.CustomerRepository;
import com.lucidity.bestRoute.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("tsp")
public class TSPBestRouteCalculatorStrategy implements BestRouteCalculatorStrategy{

    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final DistanceCalculatorStrategy distanceCalculatorStrategy;

    @Value("${average.speed}")
    private double AVERAGE_SPEED_KMPH; // Average speed in km/hr

    public TSPBestRouteCalculatorStrategy(RestaurantRepository restaurantRepository, CustomerRepository customerRepository, DistanceCalculatorStrategy distanceCalculatorStrategy) {
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
        this.distanceCalculatorStrategy = distanceCalculatorStrategy;
    }

    public double findHamiltonianCycle(Map<Long, Boolean> restaurantVisited, Map<Long, Boolean> customerVisited, Map<Long, Long> customerRestaurantMap, GeoLocation start, int count_nodes, int count, double cost, double hamiltonianCycle) throws RestaurantNotFoundException {

        if (count == count_nodes)
        {
            hamiltonianCycle = Math.min(hamiltonianCycle, cost);
            return hamiltonianCycle;
        }

        for(long restaurantId : restaurantVisited.keySet()){
            if(!restaurantVisited.get(restaurantId)){
                Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);
                if(optionalRestaurant.isEmpty()){
                    throw new RestaurantNotFoundException();
                }
                Restaurant restaurant = optionalRestaurant.get();
                GeoLocation temp = restaurant.getGeoLocation();
                double time = calculateTime(start, temp);
                time+= Math.max(restaurant.getPreparationTime()-cost-time,0);
                restaurantVisited.put(restaurantId, true);
                hamiltonianCycle = findHamiltonianCycle(restaurantVisited, customerVisited, customerRestaurantMap, temp, count_nodes, count+1, cost+time, hamiltonianCycle);
                restaurantVisited.put(restaurantId, false);
            }
        }

        for(long customerId : customerVisited.keySet()){
            if(!customerVisited.get(customerId) && restaurantVisited.get(customerRestaurantMap.get(customerId))){
                GeoLocation temp = customerRepository.findById(customerId).get().getGeoLocation();
                double time = calculateTime(start, temp);
                customerVisited.put(customerId, true);
                hamiltonianCycle = findHamiltonianCycle(restaurantVisited, customerVisited, customerRestaurantMap, temp, count_nodes, count+1, cost+time, hamiltonianCycle);
                customerVisited.put(customerId, false);
            }
        }

        return hamiltonianCycle;
    }
    @Override
    public ShortestRouteResponseDto getBestRoute(GeoLocation start, List<Order> orders) throws RestaurantNotFoundException {


        Map<Long, Boolean> restaurantVisited = new HashMap<>();
        Map<Long, Boolean> customerVisited = new HashMap<>();
        Map<Long, Long> customerRestaurantMap = new HashMap<>();

        int count_nodes = 0;

        for(Order order : orders){
            restaurantVisited.put(order.getRestaurant().getId(), false);
            customerVisited.put(order.getCustomer().getId(), false);
            customerRestaurantMap.put(order.getCustomer().getId(),order.getRestaurant().getId());
            count_nodes+=2;
        }

        double hamiltonianCycle = Double.MAX_VALUE;

        // call findHamiltonianCycle() method that returns the minimum weight Hamiltonian Cycle
        hamiltonianCycle = findHamiltonianCycle(restaurantVisited, customerVisited, customerRestaurantMap, start, count_nodes, 0, 0, hamiltonianCycle);

        ShortestRouteResponseDto response = new ShortestRouteResponseDto();

        response.setResponseStatus(ResponseStatus.SUCCESS);
        response.setTime(hamiltonianCycle);

        return response;
    }

    @Override
    public double calculateTime(GeoLocation start, GeoLocation end) {
        double distance = distanceCalculatorStrategy.calculateDistance(start, end);
        return distance / AVERAGE_SPEED_KMPH * 60;
    }
}
