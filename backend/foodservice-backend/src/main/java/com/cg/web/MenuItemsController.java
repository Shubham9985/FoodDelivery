package com.cg.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.MenuItemsDTO;
import com.cg.service.MenuItemsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/menu-items")
@Validated
public class MenuItemsController {

    private final MenuItemsService menuItemsService;

    @Autowired
    public MenuItemsController(MenuItemsService menuItemsService) {
        this.menuItemsService = menuItemsService;
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<MenuItemsDTO.Response> addMenuItem(
            @Valid @RequestBody MenuItemsDTO.Request requestDTO) {
        return new ResponseEntity<>(menuItemsService.addMenuItem(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemById(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemsDTO.Response>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemsService.getAllMenuItems());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemsDTO.Response> updateMenuItem(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId,
            @Valid @RequestBody MenuItemsDTO.Request requestDTO) {
        return ResponseEntity.ok(menuItemsService.updateMenuItem(itemId, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(
            @Positive(message = "Item ID must be a positive integer")
            @PathVariable("id") Integer itemId) {
        menuItemsService.deleteMenuItem(itemId);
        return ResponseEntity.ok("Menu item with ID " + itemId + " deleted successfully.");
    }

    // ─── DERIVED QUERIES ──────────────────────────────────────────────────────

    @GetMapping("/search/name")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByName(
            @NotBlank(message = "Item name must not be blank")
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByName(itemName));
    }

    @GetMapping("/search/name/keyword")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByName(
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByName(keyword));
    }

    // FIX: service signature uses BigDecimal, not Double
    @GetMapping("/search/price/max")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMaxPrice(
            @DecimalMin(value = "0.0", inclusive = false, message = "Max price must be greater than 0")
            @RequestParam("price") BigDecimal price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMaxPrice(price));
    }

    @GetMapping("/search/price/min")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByMinPrice(
            @DecimalMin(value = "0.0", inclusive = false, message = "Min price must be greater than 0")
            @RequestParam("price") BigDecimal price) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByMinPrice(price));
    }

    @GetMapping("/search/price/range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRange(
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") BigDecimal minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") BigDecimal maxPrice) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByPriceRange(minPrice, maxPrice));
    }

    // ─── DERIVED — VIA RESTAURANT FK ──────────────────────────────────────────

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantId(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/name")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByRestaurantName(
            @NotBlank(message = "Restaurant name must not be blank")
            @RequestParam("name") String restaurantName) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurantName(restaurantName));
    }

    @GetMapping("/restaurant/{restaurantId}/search")
    public ResponseEntity<List<MenuItemsDTO.Response>> searchMenuItemsByNameInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @NotBlank(message = "Search keyword must not be blank")
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(menuItemsService.searchMenuItemsByNameInRestaurant(restaurantId, keyword));
    }

    @GetMapping("/restaurant/{restaurantId}/price-range")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsByPriceRangeInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
            @RequestParam("min") BigDecimal minPrice,
            @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
            @RequestParam("max") BigDecimal maxPrice) {
        return ResponseEntity.ok(
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(restaurantId, minPrice, maxPrice));
    }

    @GetMapping("/restaurant/{restaurantId}/exists")
    public ResponseEntity<Boolean> checkMenuItemExistsInRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId,
            @NotBlank(message = "Item name must not be blank")
            @RequestParam("name") String itemName) {
        return ResponseEntity.ok(menuItemsService.checkMenuItemExistsInRestaurant(itemName, restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/count")
    public ResponseEntity<Integer> countMenuItemsByRestaurant(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.countMenuItemsByRestaurant(restaurantId));
    }

    // ─── JPQL ─────────────────────────────────────────────────────────────────

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

    @GetMapping("/with-orders")
    public ResponseEntity<List<MenuItemsDTO.Response>> getMenuItemsWithOrders() {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithOrders());
    }

    @GetMapping("/by-order-item/{orderItemId}")
    public ResponseEntity<MenuItemsDTO.Response> getMenuItemByOrderItemId(
            @Positive(message = "Order item ID must be a positive integer")
            @PathVariable("orderItemId") Integer orderItemId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemByOrderItemId(orderItemId));
    }

    // ─── NATIVE / STATS ───────────────────────────────────────────────────────

    @GetMapping("/stats/with-restaurant/{restaurantId}")
    public ResponseEntity<List<Object[]>> getMenuItemsWithRestaurantByRestaurantId(
            @Positive(message = "Restaurant ID must be a positive integer")
            @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsWithRestaurantByRestaurantId(restaurantId));
    }

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