package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.OrderResponseDTO;
import com.cg.service.OrderService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ─── PLACE ORDER FROM CART (was missing in original controller) ──────────
    @PostMapping("/place/{customerId}")
    public ResponseEntity<OrderResponseDTO> placeOrderFromCart(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return new ResponseEntity<>(
                orderService.placeOrderFromCart(customerId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomer(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByRestaurant(
            @Positive(message = "Restaurant ID must be positive")
            @PathVariable Integer restaurantId) {
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDriver(
            @Positive(message = "Driver ID must be positive")
            @PathVariable Integer driverId) {
        return ResponseEntity.ok(orderService.getOrdersByDriver(driverId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @NotBlank(message = "Status must not be blank")
            @PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id,
            @NotBlank(message = "Status must not be blank")
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/driver/{driverId}")
    public ResponseEntity<OrderResponseDTO> assignDriver(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id,
            @Positive(message = "Driver ID must be positive")
            @PathVariable Integer driverId) {
        return ResponseEntity.ok(orderService.assignDriver(id, driverId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/coupon/{couponId}")
    public ResponseEntity<OrderResponseDTO> applyCoupon(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id,
            @Positive(message = "Coupon ID must be positive")
            @PathVariable Integer couponId) {
        return ResponseEntity.ok(orderService.applyCoupon(id, couponId));
    }

    @DeleteMapping("/{id}/coupon/{couponId}")
    public ResponseEntity<OrderResponseDTO> removeCoupon(
            @Positive(message = "Order ID must be positive")
            @PathVariable Integer id,
            @Positive(message = "Coupon ID must be positive")
            @PathVariable Integer couponId) {
        return ResponseEntity.ok(orderService.removeCoupon(id, couponId));
    }
}