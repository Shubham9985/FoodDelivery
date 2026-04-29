package com.cg.web;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Validated  // ← Required: activates constraint annotations on @PathVariable and @RequestParam
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
     *
     * @Valid → triggers all Bean Validation annotations declared on RestaurantRequestDTO fields.
     * The request is rejected with 400 before reaching the service if any field fails.
     */
    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> addRestaurant(
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {
        RestaurantResponseDTO response = restaurantService.addRestaurant(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/restaurants/{id}
     *
     * @Positive → rejects 0 and negative IDs at the HTTP boundary.
     * Needs @Validated on class to activate on @PathVariable.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    /**
     * GET /api/restaurants
     * No params — no param-level constraints needed.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    /**
     * PUT /api/restaurants/{id}
     *
     * @Positive on path var + @Valid on body — both validated before service is called.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId,
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, requestDTO));
    }

    /**
     *
     * @Positive guards against accidental delete calls with 0 or negative IDs.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("id") Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant with ID " + restaurantId + " deleted successfully.");
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS
    // =========================================================================

    /**
     * GET /api/restaurants/search/name?name=Pizza Palace
     *
     * @NotBlank → prevents empty / whitespace-only name lookups from reaching the DB.
     */
    @GetMapping("/search/name")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByName(
            @NotBlank(message = "Restaurant name must not be blank")
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(restaurantService.getRestaurantByName(restaurantName));
    }

    /**
     * GET /api/restaurants/search/name/keyword?keyword=pizza
     *
     * @NotBlank → ensures the search bar sends at least one non-whitespace character.
     */
    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByName(
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByName(keyword));
    }

    /**
     * GET /api/restaurants/search/phone?phone=9876543210
     *
     * @NotBlank → rejects empty phone strings before a DB lookup is attempted.
     */
    @GetMapping("/search/phone")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByPhone(
            @NotBlank(message = "Phone number must not be blank")
            @RequestParam("phone") String restaurantPhone) {
        return ResponseEntity.ok(restaurantService.getRestaurantByPhone(restaurantPhone));
    }

    /**
     * GET /api/restaurants/search/address?keyword=MG Road
     *
     * @NotBlank → prevents wasteful full-table scans from a blank address search.
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurantsByAddress(
            @NotBlank(message = "Address keyword must not be blank")
            @RequestParam("keyword") String address) {
        return ResponseEntity.ok(restaurantService.searchRestaurantsByAddress(address));
    }

    // =========================================================================
    // JPQL / RELATIONSHIP-BASED ENDPOINTS
    // =========================================================================

    // No params on this endpoint — no constraints needed.
    @GetMapping("/with-menu-items")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingMenuItems() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingMenuItems());
    }

    /**
     * GET /api/restaurants/by-menu-item/{itemId}
     *
     * @Positive → rejects non-positive item IDs before the service looks them up.
     */
    @GetMapping("/by-menu-item/{itemId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantByMenuItemId(
            @Positive(message = "Menu item ID must be a positive integer")
            @PathVariable("itemId") int itemId) {
        return ResponseEntity.ok(restaurantService.getRestaurantByMenuItemId(itemId));
    }

    /**
     * GET /api/restaurants/search/menu-item-name?keyword=burger
     *
     * @NotBlank → ensures a meaningful keyword is passed before triggering a LIKE query.
     */
    @GetMapping("/search/menu-item-name")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemName(
            @NotBlank(message = "Menu item name keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMenuItemName(keyword));
    }

     
    @GetMapping("/search/menu-item-price")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMenuItemPriceRange(
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") Double minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") Double maxPrice) {
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

    // No params on this endpoint — no constraints needed.
    @GetMapping("/with-ratings")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsHavingRatings() {
        return ResponseEntity.ok(restaurantService.getRestaurantsHavingRatings());
    }

    /**
     *  
     *
     * @DecimalMin(0.0) + @DecimalMax(5.0) → enforces the valid rating scale at the HTTP boundary.
     * @Validated on the class is what makes these fire on @RequestParam.
     */
    @GetMapping("/search/min-rating")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByMinAverageRating(
            @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
            @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
            @RequestParam("rating") Double minRating) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMinAverageRating(minRating));
    }

    // =========================================================================
    // NATIVE QUERY / STATS ENDPOINTS
    // No path or query on any of these — no constraints needed.
    // =========================================================================

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