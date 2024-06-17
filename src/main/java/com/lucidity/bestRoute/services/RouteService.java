package com.lucidity.bestRoute.services;

import com.lucidity.bestRoute.dto.ResponseStatus;
import com.lucidity.bestRoute.dto.ShortestRouteResponseDto;
import com.lucidity.bestRoute.models.*;
import com.lucidity.bestRoute.repositories.CustomerRepository;
import com.lucidity.bestRoute.repositories.DeliveryExecutiveRepository;
import com.lucidity.bestRoute.repositories.OrderRepository;
import com.lucidity.bestRoute.repositories.RestaurantRepository;
import com.lucidity.bestRoute.strategies.DistanceCalculatorStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {
    @Value("${average.speed}")
    private double AVERAGE_SPEED_KMPH; // Average speed in km/hr
    private final OrderRepository orderRepository;
    private final DeliveryExecutiveRepository deliveryExecutiveRepository;
    private final DistanceCalculatorStrategy distanceCalculatorStrategy;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;

    public RouteService(OrderRepository orderRepository, DeliveryExecutiveRepository deliveryExecutiveRepository, DistanceCalculatorStrategy distanceCalculatorStrategy,
                        RestaurantRepository restaurantRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.deliveryExecutiveRepository = deliveryExecutiveRepository;
        this.distanceCalculatorStrategy = distanceCalculatorStrategy;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
    }

    public double calculateTime(GeoLocation start, GeoLocation end) {
        double distance = distanceCalculatorStrategy.calculateDistance(start, end);
        return distance / AVERAGE_SPEED_KMPH * 60;
    }

    public ShortestRouteResponseDto getMST(GeoLocation start, Map<Long, Boolean> restaurantFlag, Map<Long, Boolean> customerFlag, Map<Long, Long> customerRestaurantMap){

        int deliveryTime = 0;

        StringBuilder path = new StringBuilder("");

        ShortestRouteResponseDto response = new ShortestRouteResponseDto();

        PriorityQueue<Node> queue = new PriorityQueue<>((node1, node2) -> (int) (node1.getDistance() - node2.getDistance()));

        for(Long restaurantId : restaurantFlag.keySet()){
            Restaurant restaurant = restaurantRepository.findById(restaurantId).get();
            queue.add(new Node(restaurantId, calculateTime(start, restaurant.getGeoLocation())));
        }

        while(queue.size()>0){
            double distance = queue.peek().getDistance();
            long id = queue.peek().getId();
            GeoLocation current = null;
            queue.remove();

            if(restaurantFlag.get(id)!=null && restaurantFlag.get(id)){
                continue;
            }
            else if(customerFlag.get(id)!=null && customerFlag.get(id)){
                continue;
            }

            if(restaurantFlag.get(id)!=null){
                restaurantFlag.put(id, true);
                Restaurant restaurant = restaurantRepository.findById(id).get();
                current = restaurant.getGeoLocation();
                path.append(restaurant.getName()).append(" ");
            }
            else{
                customerFlag.put(id, true);
                Customer customer = customerRepository.findById(id).get();
                current = customer.getGeoLocation();
                path.append(customer.getName()).append(" ");
            }

            deliveryTime+=distance;

            for(long restaurantId : restaurantFlag.keySet()){
                if(!restaurantFlag.get(restaurantId)){
                    GeoLocation temp = restaurantRepository.findById(restaurantId).get().getGeoLocation();
                    queue.add(new Node(restaurantId, calculateTime(current, temp)));
                }
            }

            for(long customerId : customerFlag.keySet()){
                if(!customerFlag.get(customerId) && restaurantFlag.get(customerRestaurantMap.get(customerId))){
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

    public ShortestRouteResponseDto findShortestDeliveryTime(long deliveryExecutiveId, List<Long> orderIds) {

        Optional<DeliveryExecutive> deliveryExecutiveOptional = deliveryExecutiveRepository.findById(deliveryExecutiveId);
        DeliveryExecutive deliveryExecutive = deliveryExecutiveOptional.get();
        GeoLocation start = deliveryExecutive.getGeoLocation();
        List<Order> orders = orderRepository.findAllById(orderIds);

        Map<Long, Boolean> restaurantFlag = new HashMap<>();
        Map<Long, Boolean> customerFlag = new HashMap<>();
        Map<Long, Long> customerRestaurantMap = new HashMap<>();

        for(Order order : orders){
            restaurantFlag.put(order.getRestaurant().getId(), false);
            customerFlag.put(order.getCustomer().getId(), false);
            customerRestaurantMap.put(order.getCustomer().getId(),order.getRestaurant().getId());
        }

        return getMST(start, restaurantFlag, customerFlag, customerRestaurantMap);
    }
}
