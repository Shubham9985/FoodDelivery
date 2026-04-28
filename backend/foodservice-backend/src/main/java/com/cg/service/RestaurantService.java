package com.cg.service;

import com.cg.dto.RestaurantRequestDTO;
import com.cg.dto.RestaurantResponseDTO;

import java.util.List;

public interface RestaurantService {

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    RestaurantResponseDTO addRestaurant(RestaurantRequestDTO requestDTO);

    RestaurantResponseDTO getRestaurantById(Integer restaurantId);

    List<RestaurantResponseDTO> getAllRestaurants();

    RestaurantResponseDTO updateRestaurant(Integer restaurantId, RestaurantRequestDTO requestDTO);

    void deleteRestaurant(Integer restaurantId);

    // ─── Derived Query Methods ────────────────────────────────────────────────

    RestaurantResponseDTO getRestaurantByName(String restaurantName);

    List<RestaurantResponseDTO> searchRestaurantsByName(String keyword);

    RestaurantResponseDTO getRestaurantByPhone(String restaurantPhone);

    List<RestaurantResponseDTO> searchRestaurantsByAddress(String address);

    // ─── JPQL / Relationship-based Methods ───────────────────────────────────

    List<RestaurantResponseDTO> getRestaurantsHavingMenuItems();

    RestaurantResponseDTO getRestaurantByMenuItemId(int itemId);

    List<RestaurantResponseDTO> getRestaurantsByMenuItemName(String keyword);

    List<RestaurantResponseDTO> getRestaurantsByMenuItemPriceRange(Double minPrice, Double maxPrice);

    List<RestaurantResponseDTO> getRestaurantsHavingOrders();

    RestaurantResponseDTO getRestaurantByOrderId(Integer orderId);

    List<RestaurantResponseDTO> getRestaurantsHavingRatings();

    List<RestaurantResponseDTO> getRestaurantsByMinAverageRating(Double minRating);

    // ─── Native Query Methods ─────────────────────────────────────────────────

    List<Object[]> getAllWithMenuItemCount();

    List<Object[]> getAllWithOrderCount();

    List<Object[]> getAllWithAverageRating();

    List<Object[]> getAllWithFullSummary();
}