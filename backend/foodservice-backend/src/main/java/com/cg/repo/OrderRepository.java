package com.cg.repo;

import com.cg.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerCustomerId(Integer customerId);
    List<Order> findByRestaurantRestaurantId(Integer restaurantId);
    List<Order> findByDeliveryDriverDriverId(Integer driverId);
    List<Order> findByOrderStatus(String orderStatus);
}