package com.lucidity.bestRoute;

import com.lucidity.bestRoute.models.GeoLocation;
import com.lucidity.bestRoute.strategies.HaversineDistanceCalculatorStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BestRouteApplication {


	public static void main(String[] args) {

		SpringApplication.run(BestRouteApplication.class, args);
	}

}
