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

import com.cg.dto.MenuItemsDTO;
import com.cg.service.MenuItemsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/menu-items")
@Validated  // ← Required: activates constraint annotations on @PathVariable and @RequestParam
public class MenuItemsController {

    private final MenuItemsService menuItemsService;

    @Autowired
    public MenuItemsController(MenuItemsService menuItemsService) {
        this.menuItemsService = menuItemsService;
    }

    // =========================================================================
    // CRUD ENDPOINTS
    // =========================================================================

    /**
     * POST /api/menu-items
     *
     * @Valid → triggers all Bean Validation annotations on MenuItemsDTO.Request fields.
     * itemId is excluded from the request body — DB assigns it via @GeneratedValue(IDENTITY).
     * restaurantId in the body is validated as @NotNull @Positive in the DTO itself.
     */
    @PostMapping
    public ResponseEntity<MenuItemsDTO.Response> addMenuItem(
            @Valid @RequestBody MenuItemsDTO.Request requestDTO) {
        MenuItemsDTO.Response response = menuItemsService.addMenuItem(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/menu-items/{id}
     *
     * @Positive → rejects 0 and negative item IDs at the HTTP boundary.
     * itemId is @GeneratedValue(IDENTITY) so valid IDs always start from 1.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemById(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemById(itemId));
    }

    /**
     * GET /api/menu-items
     * No params — no param-level constraints needed.
     */
    @GetMapping
    public ResponseEntity<List<MenuItemsDTO.Response>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemsService.getAllMenuItems());
    }

    /**
     * PUT /api/menu-items/{id}
     *
     * @Positive on path var + @Valid on body — both validated before service is called.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> updateMenuItem(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId,
            @Valid @RequestBody MenuItemsDTO.Request requestDTO) {
        return ResponseEntity.ok(menuItemsService.updateMenuItem(itemId, requestDTO));
    }

    /**
     * DELETE /api/menu-items/{id}
     *
     * @Positive → prevents delete calls with 0 or negative IDs reaching the service.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId) {
        menuItemsService.deleteMenuItem(itemId);
        return ResponseEntity.ok("Menu item with ID " + itemId + " deleted successfully.");
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS — based on MenuItems columns only
    // =========================================================================

    /**
     * GET /api/menu-items/search/name?name=Paneer Tikka
     *
     * @NotBlank → prevents empty / whitespace-only name lookups from reaching the DB.
     */
    @GetMapping("/search/name")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByName(
            @NotBlank(message = "Item name must not be blank")
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByName(itemName));
    }

    /**
     * GET /api/menu-items/search/name/keyword?keyword=tikka
     *
     * @NotBlank → ensures the search bar sends at least one non-whitespace character.
     */
    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByName(
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByName(keyword));
    }

    /**
     * GET /api/menu-items/search/price/max?price=200
     *
     * @DecimalMin(exclusive) → rejects zero and negative max-price filters.
     */
    @GetMapping("/search/price/max")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMaxPrice(
            @DecimalMin(value = "0.0", inclusive = false, message = "Max price must be greater than 0")
            @RequestParam("price") Double price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMaxPrice(price));
    }

    /**
     * GET /api/menu-items/search/price/min?price=100
     *
     * @DecimalMin(exclusive) → rejects zero and negative min-price filters.
     */
    @GetMapping("/search/price/min")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMinPrice(
            @DecimalMin(value = "0.0", inclusive = false, message = "Min price must be greater than 0")
            @RequestParam("price") Double price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMinPrice(price));
    }

    /**
     * GET /api/menu-items/search/price/range?min=50&max=300
     *
     * @DecimalMin(exclusive) on both params → rejects zero or negative price bounds.
     * Cross-field check (min ≤ max) is deliberately left to the service layer.
     */
    @GetMapping("/search/price/range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRange(
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") Double minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") Double maxPrice) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByPriceRange(minPrice, maxPrice));
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS — via Restaurant FK
    // =========================================================================

    /**
     * GET /api/menu-items/restaurant/{restaurantId}
     *
     * @Positive → rejects non-positive restaurant IDs before any DB access.
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantId(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantId(restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/name?name=Pizza Palace
     *
     * @NotBlank → prevents empty restaurant name lookups from reaching the DB.
     */
    @GetMapping("/restaurant/name")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantName(
            @NotBlank(message = "Restaurant name must not be blank")
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantName(restaurantName));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/search?keyword=burger
     *
     * @Positive on restaurantId + @NotBlank on keyword — both enforced together.
     */
    @GetMapping("/restaurant/{restaurantId}/search")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByNameInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByNameInRestaurant(restaurantId, keyword));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/price-range?min=50&max=300
     *
     * @Positive on restaurantId + @DecimalMin(exclusive) on both price bounds.
     * Cross-field check (min ≤ max) is handled in the service layer.
     */
    @GetMapping("/restaurant/{restaurantId}/price-range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRangeInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") Double minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") Double maxPrice) {
        return ResponseEntity.ok(
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(restaurantId, minPrice, maxPrice));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/exists?name=Burger
     *
     * @Positive on restaurantId + @NotBlank on item name.
     */
    @GetMapping("/restaurant/{restaurantId}/exists")
    public ResponseEntity<Boolean> checkMenuItemExistsInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @NotBlank(message = "Item name must not be blank")
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.checkMenuItemExistsInRestaurant(itemName, restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/count
     *
     * @Positive → rejects invalid restaurant IDs before the count query runs.
     */
    @GetMapping("/restaurant/{restaurantId}/count")
    public ResponseEntity<Integer> countMenuItemsByRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.countMenuItemsByRestaurant(restaurantId));
    }

    // =========================================================================
    // JPQL ENDPOINTS
    // =========================================================================

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/sorted/price-asc
     * GET /api/menu-items/restaurant/{restaurantId}/sorted/price-desc
     * GET /api/menu-items/restaurant/{restaurantId}/most-expensive
     * GET /api/menu-items/restaurant/{restaurantId}/cheapest
     *
     * All four share the same constraint: @Positive on restaurantId.
     */
    @GetMapping("/restaurant/{restaurantId}/sorted/price-asc")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsSortedByPriceAsc(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantIdOrderByPriceAsc(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/sorted/price-desc")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsSortedByPriceDesc(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantIdOrderByPriceDesc(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/most-expensive")
    public ResponseEntity<MenuItemsDTO.Response> getMostExpensiveMenuItemByRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMostExpensiveMenuItemByRestaurant(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/cheapest")
    public ResponseEntity<MenuItemsDTO.Response> getCheapestMenuItemByRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getCheapestMenuItemByRestaurant(restaurantId));
    }

    /**
     * GET /api/menu-items/with-orders
     * No params — no constraints needed.
     */
    @GetMapping("/with-orders")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsWithOrders() {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithOrders());
    }

    /**
     * GET /api/menu-items/by-order-item/{orderItemId}
     *
     * @Positive → rejects non-positive orderItem IDs before service lookup.
     */
    @GetMapping("/by-order-item/{orderItemId}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByOrderItemId(
            @Positive(message = "Order item ID must be a positive integer")
            @PathVariable("orderItemId") Integer orderItemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByOrderItemId(orderItemId));
    }

    // =========================================================================
    // NATIVE QUERY / STATS ENDPOINTS
    // =========================================================================

    /**
     * GET /api/menu-items/stats/with-restaurant/{restaurantId}
     *
     * @Positive → only constraint needed here; the rest of the query is fixed.
     */
    @GetMapping("/stats/with-restaurant/{restaurantId}")
    public ResponseEntity<List<Object[]>> getMenuItemsWithRestaurantByRestaurantId(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithRestaurantByRestaurantId(restaurantId));
    }

    /**
     * GET /api/menu-items/stats/avg-price-per-restaurant
     * GET /api/menu-items/stats/order-count
     * GET /api/menu-items/stats/most-ordered
     *
     * No path or query params on these — no constraints needed.
     */
    @GetMapping("/stats/avg-price-per-restaurant")
    public ResponseEntity<List<Object[]>> getAveragePricePerRestaurant() {
        return ResponseEntity.ok(menuItemsService.getAveragePricePerRestaurant());
    }

    @GetMapping("/stats/order-count")
    public ResponseEntity<List<Object[]>> getMenuItemsWithOrderCount() {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithOrderCount());
    }

    @GetMapping("/stats/most-ordered")
    public ResponseEntity<List<Object[]>> getMostOrderedMenuItems() {
        return ResponseEntity.ok(menuItemsService.getMostOrderedMenuItems());
    }
}