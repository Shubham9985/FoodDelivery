package com.cg.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerCustomerId(Integer customerId);
    List<Order> findByRestaurantRestaurantId(Integer restaurantId);
    List<Order> findByDeliveryDriverDriverId(Integer driverId);
    List<Order> findByOrderStatusIgnoreCase(String status);
}