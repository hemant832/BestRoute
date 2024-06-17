# Optimal Delivery Route Solution

This project implements a solution to determine the optimal route for a delivery executive to deliver multiple orders in the shortest possible time. The solution follows the MVC architecture and uses the Spring Boot framework. It persists data in an in-memory H2 database.

The project employs strategy and factory design patterns along with OOPS concepts to achieve readability, modularity, and extensibility. The solution is scalable to handle any number of restaurants and customers.

## Project Structure

### Controller
Handles HTTP requests and responses.

### Service
Contains business logic for calculating the best route.

### Repository
Manages database interactions.

### Model
Defines the data entities.

## Approach

### 1. Distance Calculation
Implemented a `DistanceCalculator` strategy interface that uses the Haversine formula to calculate distances between geographical points.

### 2. Shortest Path Calculation Using MST
Implemented a `BestRouteCalculator` strategy interface To determine the shortest delivery route, the algorithm constructs a Minimum Spanning Tree (MST) of the locations (delivery executive, restaurants, and customers).

### 3. MVC Architecture

#### Controller
`RouteController` handles incoming list of orders and a delivery executive and forwards them to the `RouteService` layer.

#### Service
`RouteService` contains the logic to calculate the shortest delivery time using the minimum spanning tree approach.

#### Repository
Interfaces for all the repositories extend `JpaRepository`.

#### Model
Entities such as `User`, `Restaurant`, and `Order`.

## Database Configuration

### H2 Database
Used for data persistence during development.

### Tables
- Customer
- Restaurant
- Order
- Delivery Executive

Have added some samples (restaurants, customers, orders) for simplicity. Sample data can be found under `data.sql` in resources. Orders, restaurants, and customers can be added using respective controllers as well.

## API Contract

### Request
Takes a list of orders and a delivery executive as input for the Route controller.

```bash
curl --location 'http://localhost:8080/api/delivery/shortest-route' \
--header 'Content-Type: application/json' \
--data '{
  "deliveryExecutiveId": 33,
  "orderIds": [
    1,
    2
  ]
}'
```

### Response

```bash
{
    "time": 4.904697194844616,
    "bestRoute": "R2 C2 R1 C1",
    "responseStatus": "SUCCESS"
}
```

Here : 
- time -> best Route time in minutes
- bestRoute -> best Route Path
- responseStatus -> status

## Running the Application

```bash
Build: mvn clean install
Run: mvn spring-boot:run
Access: The application runs on http://localhost:8080
```