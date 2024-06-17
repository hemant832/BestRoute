package com.lucidity.bestRoute.strategies;

import com.lucidity.bestRoute.dto.ResponseStatus;
import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.models.*;
import com.lucidity.bestRoute.repositories.CustomerRepository;
import com.lucidity.bestRoute.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Component
@Primary
public class MSTBestRouteCalculatorStrategy implements BestRouteCalculatorStrategy{
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final DistanceCalculatorStrategy distanceCalculatorStrategy;

    @Value("${average.speed}")
    private double AVERAGE_SPEED_KMPH; // Average speed in km/hr

    public MSTBestRouteCalculatorStrategy(RestaurantRepository restaurantRepository, CustomerRepository customerRepository, DistanceCalculatorStrategy distanceCalculatorStrategy) {
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
        this.distanceCalculatorStrategy = distanceCalculatorStrategy;
    }

    @Override
    public ShortestRouteResponseDto getBestRoute(GeoLocation start, List<Order> orders) {

        // create required maps for Minimum spanning tree Algo
        Map<Long, Boolean> restaurantVisited = new HashMap<>();
        Map<Long, Boolean> customerVisited = new HashMap<>();
        Map<Long, Long> customerRestaurantMap = new HashMap<>();

        for(Order order : orders){
            restaurantVisited.put(order.getRestaurant().getId(), false);
            customerVisited.put(order.getCustomer().getId(), false);
            customerRestaurantMap.put(order.getCustomer().getId(),order.getRestaurant().getId());
        }

        // start delivery
        int deliveryTime = 0;

        StringBuilder path = new StringBuilder("");

        ShortestRouteResponseDto response = new ShortestRouteResponseDto();

        PriorityQueue<Node> queue = new PriorityQueue<>((node1, node2) -> (int) (node1.getTime() - node2.getTime()));

        // store all the restaurants in the priority queue
        for(Long restaurantId : restaurantVisited.keySet()){
            Restaurant restaurant = restaurantRepository.findById(restaurantId).get();
            queue.add(new Node(restaurantId, calculateTime(start, restaurant.getGeoLocation())));
        }

        // loop till queue is empty
        while(queue.size()>0){
            double distance = queue.peek().getTime();
            long id = queue.peek().getId();
            GeoLocation current = null;
            queue.remove();

            // continue if restaurant or customer is visited
            if(restaurantVisited.containsKey(id) && restaurantVisited.get(id)){
                continue;
            }
            else if(customerVisited.containsKey(id) && customerVisited.get(id)){
                continue;
            }

            if(restaurantVisited.containsKey(id)){
                restaurantVisited.put(id, true);
                Restaurant restaurant = restaurantRepository.findById(id).get();
                current = restaurant.getGeoLocation();
                path.append(restaurant.getName()).append(" ");
            }
            else{
                customerVisited.put(id, true);
                Customer customer = customerRepository.findById(id).get();
                current = customer.getGeoLocation();
                path.append(customer.getName()).append(" ");
            }

            deliveryTime+=distance;

            // for each id we can add all the not visited restaurants and all not visited customers whose restaurants are visited

            for(long restaurantId : restaurantVisited.keySet()){
                if(!restaurantVisited.get(restaurantId)){
                    GeoLocation temp = restaurantRepository.findById(restaurantId).get().getGeoLocation();
                    queue.add(new Node(restaurantId, calculateTime(current, temp)));
                }
            }

            for(long customerId : customerVisited.keySet()){
                if(!customerVisited.get(customerId) && restaurantVisited.get(customerRestaurantMap.get(customerId))){
                    GeoLocation temp = customerRepository.findById(customerId).get().getGeoLocation();
                    queue.add(new Node(customerId, calculateTime(current, temp)));
                }
            }
        }

        response.setResponseStatus(ResponseStatus.SUCCESS);
        response.setTime(deliveryTime);
        response.setBestRoute(path.substring(0, path.length()-1));

        return response;
    }

    @Override
    public double calculateTime(GeoLocation start, GeoLocation end) {
        double distance = distanceCalculatorStrategy.calculateDistance(start, end);
        return distance / AVERAGE_SPEED_KMPH * 60;
    }
}
