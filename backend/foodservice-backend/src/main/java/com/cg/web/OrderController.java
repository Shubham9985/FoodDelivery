package com.cg.web;


import com.cg.dto.OrderResponseDTO;
import com.cg.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public OrderResponseDTO getOrder(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponseDTO> getOrdersByCustomer(@PathVariable Integer customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderResponseDTO> getOrdersByRestaurant(@PathVariable Integer restaurantId) {
        return orderService.getOrdersByRestaurant(restaurantId);
    }

    @GetMapping("/driver/{driverId}")
    public List<OrderResponseDTO> getOrdersByDriver(@PathVariable Integer driverId) {
        return orderService.getOrdersByDriver(driverId);
    }

    @GetMapping("/status/{status}")
    public List<OrderResponseDTO> getOrdersByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    @PutMapping("/{id}/driver/{driverId}")
    public OrderResponseDTO assignDriver(
            @PathVariable Integer id,
            @PathVariable Integer driverId) {
        return orderService.assignDriver(id, driverId);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Integer id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("/{id}/coupon/{couponId}")
    public OrderResponseDTO applyCoupon(
            @PathVariable Integer id,
            @PathVariable Integer couponId) {
        return orderService.applyCoupon(id, couponId);
    }

    @PutMapping("/{id}/coupon/remove/{couponId}")
    public OrderResponseDTO removeCoupon(
            @PathVariable Integer id,
            @PathVariable Integer couponId) {
        return orderService.removeCoupon(id, couponId);
    }
}
