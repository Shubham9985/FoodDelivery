package com.cg.web;


import com.cg.dto.MenuItemsDTO;
import com.cg.service.MenuItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
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
     * Create a new menu item. itemId is @GeneratedValue(IDENTITY) — DB assigns it.
     * Body must include restaurantId (FK) to link the item to a restaurant.
     */
    @PostMapping
    public ResponseEntity<MenuItemsDTO.Response> addMenuItem(
            @RequestBody MenuItemsDTO.Request requestDTO) {
        MenuItemsDTO.Response response = menuItemsService.addMenuItem(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/menu-items/{id}
     * Fetch a single menu item by its primary key.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemById(
            @PathVariable("id") Integer itemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemById(itemId));
    }

    /**
     * GET /api/menu-items
     * Fetch all menu items across all restaurants.
     */
    @GetMapping
    public ResponseEntity<List<MenuItemsDTO.Response>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemsService.getAllMenuItems());
    }

    /**
     * PUT /api/menu-items/{id}
     * Update a menu item's name, description, price, or restaurant FK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> updateMenuItem(
            @PathVariable("id") Integer itemId,
            @RequestBody MenuItemsDTO.Request requestDTO) {
        return ResponseEntity.ok(menuItemsService.updateMenuItem(itemId, requestDTO));
    }

    /**
     * DELETE /api/menu-items/{id}
     * Delete a menu item by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable("id") Integer itemId) {
        menuItemsService.deleteMenuItem(itemId);
        return ResponseEntity.ok("Menu item with ID " + itemId + " deleted successfully.");
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS — based on MenuItems columns only
    // =========================================================================

    /**
     * GET /api/menu-items/search/name?name=Paneer Tikka
     * Find by exact item name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByName(
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByName(itemName));
    }

    /**
     * GET /api/menu-items/search/name/keyword?keyword=tikka
     * Case-insensitive keyword search on item name (for search bar).
     */
    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByName(
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByName(keyword));
    }

    /**
     * GET /api/menu-items/search/price/max?price=200
     * All items priced at or below the given value.
     */
    @GetMapping("/search/price/max")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMaxPrice(
            @RequestParam("price") Double price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMaxPrice(price));
    }

    /**
     * GET /api/menu-items/search/price/min?price=100
     * All items priced at or above the given value.
     */
    @GetMapping("/search/price/min")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMinPrice(
            @RequestParam("price") Double price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMinPrice(price));
    }

    /**
     * GET /api/menu-items/search/price/range?min=50&max=300
     * All items within the given price range.
     */
    @GetMapping("/search/price/range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRange(
            @RequestParam("min") Double minPrice,
            @RequestParam("max") Double maxPrice) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByPriceRange(minPrice, maxPrice));
    }

    // =========================================================================
    // DERIVED QUERY ENDPOINTS — via Restaurant FK
    // =========================================================================

    /**
     * GET /api/menu-items/restaurant/{restaurantId}
     * All menu items belonging to a specific restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantId(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantId(restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/name?name=Pizza Palace
     * All menu items belonging to a restaurant by its name.
     */
    @GetMapping("/restaurant/name")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantName(
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantName(restaurantName));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/search?keyword=burger
     * Search menu items by name keyword within a specific restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/search")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByNameInRestaurant(
            @PathVariable("restaurantId") Integer restaurantId,
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByNameInRestaurant(restaurantId, keyword));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/price-range?min=50&max=300
     * Menu items within a price range for a specific restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/price-range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRangeInRestaurant(
            @PathVariable("restaurantId") Integer restaurantId,
            @RequestParam("min") Double minPrice,
            @RequestParam("max") Double maxPrice) {
        return ResponseEntity.ok(
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(restaurantId, minPrice, maxPrice));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/exists?name=Burger
     * Check if a menu item with the given name exists in the given restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/exists")
    public ResponseEntity<Boolean> checkMenuItemExistsInRestaurant(
            @PathVariable("restaurantId") Integer restaurantId,
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.checkMenuItemExistsInRestaurant(itemName, restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/count
     * Total number of menu items in a specific restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/count")
    public ResponseEntity<Integer> countMenuItemsByRestaurant(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.countMenuItemsByRestaurant(restaurantId));
    }

    // =========================================================================
    // JPQL ENDPOINTS
    // =========================================================================

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/sorted/price-asc
     * All menu items of a restaurant sorted by price ascending.
     */
    @GetMapping("/restaurant/{restaurantId}/sorted/price-asc")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsSortedByPriceAsc(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantIdOrderByPriceAsc(restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/sorted/price-desc
     * All menu items of a restaurant sorted by price descending.
     */
    @GetMapping("/restaurant/{restaurantId}/sorted/price-desc")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsSortedByPriceDesc(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantIdOrderByPriceDesc(restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/most-expensive
     * The single most expensive menu item in a restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/most-expensive")
    public ResponseEntity<MenuItemsDTO.Response> getMostExpensiveMenuItemByRestaurant(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMostExpensiveMenuItemByRestaurant(restaurantId));
    }

    /**
     * GET /api/menu-items/restaurant/{restaurantId}/cheapest
     * The single cheapest menu item in a restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}/cheapest")
    public ResponseEntity<MenuItemsDTO.Response> getCheapestMenuItemByRestaurant(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getCheapestMenuItemByRestaurant(restaurantId));
    }

    /**
     * GET /api/menu-items/with-orders
     * All menu items that have been ordered at least once.
     */
    @GetMapping("/with-orders")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsWithOrders() {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithOrders());
    }

    /**
     * GET /api/menu-items/by-order-item/{orderItemId}
     * The menu item linked to a specific OrderItem.
     */
    @GetMapping("/by-order-item/{orderItemId}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByOrderItemId(
            @PathVariable("orderItemId") Integer orderItemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByOrderItemId(orderItemId));
    }

    // =========================================================================
    // NATIVE QUERY ENDPOINTS — returns raw aggregated data
    // =========================================================================

    /**
     * GET /api/menu-items/stats/with-restaurant/{restaurantId}
     * Menu items joined with restaurant details for a given restaurant.
     * Raw Object[] columns: [item_id, item_name, item_description, item_price,
     *                        restaurant_id, restaurant_name]
     */
    @GetMapping("/stats/with-restaurant/{restaurantId}")
    public ResponseEntity<List<Object[]>> getMenuItemsWithRestaurantByRestaurantId(
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithRestaurantByRestaurantId(restaurantId));
    }

    /**
     * GET /api/menu-items/stats/avg-price-per-restaurant
     * Average item price grouped by restaurant.
     * Raw Object[] columns: [restaurant_id, restaurant_name, avg_price]
     */
    @GetMapping("/stats/avg-price-per-restaurant")
    public ResponseEntity<List<Object[]>> getAveragePricePerRestaurant() {
        return ResponseEntity.ok(menuItemsService.getAveragePricePerRestaurant());
    }

    /**
     * GET /api/menu-items/stats/order-count
     * Each menu item with how many times it has been ordered, sorted desc.
     * Raw Object[] columns: [item_id, item_name, item_price, times_ordered]
     */
    @GetMapping("/stats/order-count")
    public ResponseEntity<List<Object[]>> getMenuItemsWithOrderCount() {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithOrderCount());
    }

    /**
     * GET /api/menu-items/stats/most-ordered
     * Most ordered menu items across all restaurants, sorted by order count desc.
     * Raw Object[] columns: [item_id, item_name, restaurant_name, times_ordered]
     */
    @GetMapping("/stats/most-ordered")
    public ResponseEntity<List<Object[]>> getMostOrderedMenuItems() {
        return ResponseEntity.ok(menuItemsService.getMostOrderedMenuItems());
    }
}
