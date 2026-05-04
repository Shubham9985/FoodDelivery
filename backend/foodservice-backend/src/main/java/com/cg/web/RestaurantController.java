package com.cg.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.RestaurantRequestDTO;
import com.cg.dto.RestaurantResponseDTO;
import com.cg.service.RestaurantService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/restaurants")
@Validated
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> addRestaurant(
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {
        return new ResponseEntity<>(restaurantService.addRestaurant(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId,
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant with ID " + restaurantId + " deleted successfully.");
    }

    // ─── DERIVED QUERIES ──────────────────────────────────────────────────────

    @GetMapping("/search/name")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByName(
            @NotBlank(message = "Restaurant name must not be blank")
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(restaurantService.getRestaurantByName(restaurantName));
    }

    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByName(
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByName(keyword));
    }

    @GetMapping("/search/phone")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByPhone(
            @NotBlank(message = "Phone number must not be blank")
            @RequestParam("phone") String restaurantPhone) {
        return ResponseEntity.ok(restaurantService.getRestaurantByPhone(restaurantPhone));
    }

    @GetMapping("/search/address")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByAddress(
            @NotBlank(message = "Address keyword must not be blank")
            @RequestParam("keyword") String address) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByAddress(address));
    }

    // ─── JPQL / RELATIONSHIP-BASED ────────────────────────────────────────────

    @GetMapping("/with-menu-items")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingMenuItems() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingMenuItems());
    }

    @GetMapping("/by-menu-item/{itemId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByMenuItemId(
            @Positive(message = "Menu item ID must be a positive integer")
            @PathVariable("itemId") int itemId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByMenuItemId(itemId));
    }

    @GetMapping("/search/menu-item-name")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemName(
            @NotBlank(message = "Menu item name keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMenuItemName(keyword));
    }

    // FIX: service signature uses BigDecimal, not Double
    @GetMapping("/search/menu-item-price")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemPriceRange(
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") BigDecimal minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") BigDecimal maxPrice) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMenuItemPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/with-orders")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingOrders() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingOrders());
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByOrderId(
            @Positive(message = "Order ID must be a positive integer")
            @PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByOrderId(orderId));
    }

    @GetMapping("/with-ratings")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingRatings() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingRatings());
    }

    @GetMapping("/search/min-rating")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMinAverageRating(
            @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
            @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
            @RequestParam("rating") Double minRating) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMinAverageRating(minRating));
    }

    // ─── NATIVE / STATS ───────────────────────────────────────────────────────

    @GetMapping("/stats/menu-item-count")
    public ResponseEntity<List<Object[]>> getAllWithMenuItemCount() {
        return ResponseEntity.ok(restaurantService.getAllWithMenuItemCount());
    }

    @GetMapping("/stats/order-count")
    public ResponseEntity<List<Object[]>> getAllWithOrderCount() {
        return ResponseEntity.ok(restaurantService.getAllWithOrderCount());
    }

    @GetMapping("/stats/average-rating")
    public ResponseEntity<List<Object[]>> getAllWithAverageRating() {
        return ResponseEntity.ok(restaurantService.getAllWithAverageRating());
    }

    @GetMapping("/stats/full-summary")
    public ResponseEntity<List<Object[]>> getAllWithFullSummary() {
        return ResponseEntity.ok(restaurantService.getAllWithFullSummary());
    }
}