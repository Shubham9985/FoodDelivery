package com.cg.web;


import com.cg.dto.RestaurantRequestDTO;
import com.cg.dto.RestaurantResponseDTO;
import com.cg.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // =========================================================================
    // CRUD ENDPOINTS
    // =========================================================================

    /**
     * POST /api/restaurants
     * Add a new restaurant. restaurantId must be supplied in body (no @GeneratedValue).
     */
    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> addRestaurant(
            @RequestBody RestaurantRequestDTO requestDTO) {
        RestaurantResponseDTO response = restaurantService.addRestaurant(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/restaurants/{id}
     * Fetch a single restaurant by its primary key.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(
            @PathVariable("id") Integer restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    /**
     * GET /api/restaurants
     * Fetch all restaurants.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    /**
     * PUT /api/restaurants/{id}
     * Update scalar fields of an existing restaurant. Relationships are untouched.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable("id") Integer restaurantId,
            @RequestBody RestaurantRequestDTO requestDTO) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, requestDTO));
    }

    /**
     * DELETE /api/restaurants/{id}
     * Delete a restaurant. CascadeType.ALL removes its menuItems and ratings.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable("id") Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant with ID " + restaurantId + " deleted successfully.");
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS
    // =========================================================================

    /**
     * GET /api/restaurants/search/name?name=Pizza Palace
     * Find by exact restaurant name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByName(
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(restaurantService.getRestaurantByName(restaurantName));
    }

    /**
     * GET /api/restaurants/search/name/keyword?keyword=pizza
     * Case-insensitive keyword search on restaurant name.
     */
    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByName(
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByName(keyword));
    }

    /**
     * GET /api/restaurants/search/phone?phone=9876543210
     * Find by exact phone number.
     */
    @GetMapping("/search/phone")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByPhone(
            @RequestParam("phone") String restaurantPhone) {
        return ResponseEntity.ok(restaurantService.getRestaurantByPhone(restaurantPhone));
    }

    /**
     * GET /api/restaurants/search/address?keyword=MG Road
     * Case-insensitive keyword search on restaurant address.
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByAddress(
            @RequestParam("keyword") String address) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByAddress(address));
    }

    // =========================================================================
    // JPQL / RELATIONSHIP-BASED ENDPOINTS
    // =========================================================================

    /**
     * GET /api/restaurants/with-menu-items
     * All restaurants that have at least one menu item.
     */
    @GetMapping("/with-menu-items")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingMenuItems() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingMenuItems());
    }

    /**
     * GET /api/restaurants/by-menu-item/{itemId}
     * Restaurant that owns the given menu item.
     */
    @GetMapping("/by-menu-item/{itemId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByMenuItemId(
            @PathVariable("itemId") int itemId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByMenuItemId(itemId));
    }

    /**
     * GET /api/restaurants/search/menu-item-name?keyword=burger
     * Restaurants that have a menu item matching the keyword.
     */
    @GetMapping("/search/menu-item-name")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemName(
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMenuItemName(keyword));
    }

    /**
     * GET /api/restaurants/search/menu-item-price?min=50&max=300
     * Restaurants that have at least one menu item within the price range.
     */
    @GetMapping("/search/menu-item-price")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemPriceRange(
            @RequestParam("min") Double minPrice,
            @RequestParam("max") Double maxPrice) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMenuItemPriceRange(minPrice, maxPrice));
    }

    /**
     * GET /api/restaurants/with-orders
     * All restaurants that have received at least one order.
     */
    @GetMapping("/with-orders")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingOrders() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingOrders());
    }

    /**
     * GET /api/restaurants/by-order/{orderId}
     * Restaurant linked to the given order.
     */
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByOrderId(
            @PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByOrderId(orderId));
    }

    /**
     * GET /api/restaurants/with-ratings
     * All restaurants that have at least one rating.
     */
    @GetMapping("/with-ratings")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingRatings() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingRatings());
    }

    /**
     * GET /api/restaurants/search/min-rating?rating=4.0
     * Restaurants with average rating >= given value.
     */
    @GetMapping("/search/min-rating")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMinAverageRating(
            @RequestParam("rating") Double minRating) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMinAverageRating(minRating));
    }

    // =========================================================================
    // NATIVE QUERY ENDPOINTS — returns raw aggregated data
    // =========================================================================

    /**
     * GET /api/restaurants/stats/menu-item-count
     * Each restaurant with its total menu item count.
     * Raw Object[] columns: [restaurant_id, restaurant_name, restaurant_address, restaurant_phone, total_items]
     */
    @GetMapping("/stats/menu-item-count")
    public ResponseEntity<List<Object[]>> getAllWithMenuItemCount() {
        return ResponseEntity.ok(restaurantService.getAllWithMenuItemCount());
    }

    /**
     * GET /api/restaurants/stats/order-count
     * Each restaurant with its total order count.
     * Raw Object[] columns: [restaurant_id, restaurant_name, total_orders]
     */
    @GetMapping("/stats/order-count")
    public ResponseEntity<List<Object[]>> getAllWithOrderCount() {
        return ResponseEntity.ok(restaurantService.getAllWithOrderCount());
    }

    /**
     * GET /api/restaurants/stats/average-rating
     * Each restaurant with its average rating.
     * Raw Object[] columns: [restaurant_id, restaurant_name, avg_rating]
     */
    @GetMapping("/stats/average-rating")
    public ResponseEntity<List<Object[]>> getAllWithAverageRating() {
        return ResponseEntity.ok(restaurantService.getAllWithAverageRating());
    }

    /**
     * GET /api/restaurants/stats/full-summary
     * Full aggregated summary per restaurant.
     * Raw Object[] columns: [restaurant_id, restaurant_name, restaurant_address,
     *                        restaurant_phone, total_menu_items, total_orders, avg_rating]
     */
    @GetMapping("/stats/full-summary")
    public ResponseEntity<List<Object[]>> getAllWithFullSummary() {
        return ResponseEntity.ok(restaurantService.getAllWithFullSummary());
    }
}