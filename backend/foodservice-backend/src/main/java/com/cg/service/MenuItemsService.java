package com.cg.service;

import com.cg.dto.MenuItemsDTO;

import java.math.BigDecimal;
import java.util.List;

public interface MenuItemsService {

    // ─── CRUD ──────────────────────────────────────────── 
	
    MenuItemsDTO.Response addMenuItem(MenuItemsDTO.Request requestDTO);

    MenuItemsDTO.Response getMenuItemById(Integer itemId);

    List<MenuItemsDTO.Response> getAllMenuItems();

    MenuItemsDTO.Response updateMenuItem(Integer itemId, MenuItemsDTO.Request requestDTO);

    void deleteMenuItem(Integer itemId);

    // ─── Derived Query Methods ────────────────────────────────────────────────

    MenuItemsDTO.Response getMenuItemByName(String itemName);

    List<MenuItemsDTO.Response> searchMenuItemsByName(String keyword);

    List<MenuItemsDTO.Response> getMenuItemsByMaxPrice(BigDecimal price);

    List<MenuItemsDTO.Response> getMenuItemsByMinPrice(BigDecimal price);

    List<MenuItemsDTO.Response> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // ─── Derived Query Methods — via Restaurant FK ────────────────────────────

    List<MenuItemsDTO.Response> getMenuItemsByRestaurantId(Integer restaurantId);

    List<MenuItemsDTO.Response> getMenuItemsByRestaurantName(String restaurantName);

    List<MenuItemsDTO.Response> searchMenuItemsByNameInRestaurant(Integer restaurantId, String keyword);

    List<MenuItemsDTO.Response> getMenuItemsByPriceRangeInRestaurant(Integer restaurantId, BigDecimal minPrice, BigDecimal maxPrice);

    boolean checkMenuItemExistsInRestaurant(String itemName, Integer restaurantId);

    int countMenuItemsByRestaurant(Integer restaurantId);

    // ─── JPQL Methods ────────────────────────────────────────────────────────

    List<MenuItemsDTO.Response> getMenuItemsByRestaurantIdOrderByPriceAsc(Integer restaurantId);

    List<MenuItemsDTO.Response> getMenuItemsByRestaurantIdOrderByPriceDesc(Integer restaurantId);

    MenuItemsDTO.Response getMostExpensiveMenuItemByRestaurant(Integer restaurantId);

    MenuItemsDTO.Response getCheapestMenuItemByRestaurant(Integer restaurantId);

    List<MenuItemsDTO.Response> getMenuItemsWithOrders();

    MenuItemsDTO.Response getMenuItemByOrderItemId(Integer orderItemId);

    // ─── Native Query Methods ─────────────────────────────────────────────────

    List<Object[]> getMenuItemsWithRestaurantByRestaurantId(Integer restaurantId);

    List<Object[]> getAveragePricePerRestaurant();

    List<Object[]> getMenuItemsWithOrderCount();

    List<Object[]> getMostOrderedMenuItems();
}