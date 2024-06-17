package com.lucidity.bestRoute.repositories;

import com.lucidity.bestRoute.models.DeliveryExecutive;
import com.lucidity.bestRoute.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryExecutiveRepository extends JpaRepository<DeliveryExecutive, Long>{

}
