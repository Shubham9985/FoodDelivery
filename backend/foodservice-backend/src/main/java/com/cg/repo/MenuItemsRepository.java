package com.cg.repo;

import com.cg.entity.MenuItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemsRepository extends JpaRepository<MenuItems, Integer> {

    Optional<MenuItems> findByItemName(String itemName);

    List<MenuItems> findByItemNameContainingIgnoreCase(String keyword);

    List<MenuItems> findByItemPriceLessThanEqual(BigDecimal price);

    List<MenuItems> findByItemPriceGreaterThanEqual(BigDecimal price);

    List<MenuItems> findByItemPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<MenuItems> findByRestaurant_RestaurantId(Integer restaurantId);

    List<MenuItems> findByRestaurant_RestaurantName(String restaurantName);

    List<MenuItems> findByRestaurant_RestaurantIdAndItemNameContainingIgnoreCase(
            Integer restaurantId, String keyword);

    List<MenuItems> findByRestaurant_RestaurantIdAndItemPriceBetween(
            Integer restaurantId, BigDecimal minPrice, BigDecimal maxPrice);

    boolean existsByItemNameAndRestaurant_RestaurantId(String itemName, Integer restaurantId);

    int countByRestaurant_RestaurantId(Integer restaurantId);

    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice ASC")
    List<MenuItems> findByRestaurantIdOrderByPriceAsc(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice DESC")
    List<MenuItems> findByRestaurantIdOrderByPriceDesc(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice DESC LIMIT 1")
    Optional<MenuItems> findMostExpensiveByRestaurantId(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice ASC LIMIT 1")
    Optional<MenuItems> findCheapestByRestaurantId(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT DISTINCT oi.menuItem FROM OrderItem oi")
    List<MenuItems> findMenuItemsWithOrders();

    @Query("SELECT oi.menuItem FROM OrderItem oi WHERE oi.orderItemId = :orderItemId")
    Optional<MenuItems> findByOrderItemId(@Param("orderItemId") Integer orderItemId);

    @Query(value = """
            SELECT m.item_id, m.item_name, m.item_description,
                   m.item_price, r.restaurant_id, r.restaurant_name
            FROM MenuItems m
            JOIN Restaurants r ON m.restaurant_id = r.restaurant_id
            WHERE m.restaurant_id = :restaurantId
            """, nativeQuery = true)
    List<Object[]> findMenuItemsWithRestaurantByRestaurantId(
            @Param("restaurantId") Integer restaurantId);

    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   AVG(m.item_price) AS avg_price
            FROM Restaurants r
            JOIN MenuItems m ON r.restaurant_id = m.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAveragePricePerRestaurant();

    @Query(value = """
            SELECT m.item_id, m.item_name, m.item_price,
                   COUNT(oi.order_item_id) AS times_ordered
            FROM MenuItems m
            LEFT JOIN OrderItems oi ON m.item_id = oi.item_id
            GROUP BY m.item_id, m.item_name, m.item_price
            ORDER BY times_ordered DESC
            """, nativeQuery = true)
    List<Object[]> findMenuItemsWithOrderCount();

    @Query(value = """
            SELECT m.item_id, m.item_name, r.restaurant_name,
                   COUNT(oi.order_item_id) AS times_ordered
            FROM MenuItems m
            JOIN OrderItems oi ON m.item_id = oi.item_id
            JOIN Restaurants r ON m.restaurant_id = r.restaurant_id
            GROUP BY m.item_id, m.item_name, r.restaurant_name
            ORDER BY times_ordered DESC
            """, nativeQuery = true)
    List<Object[]> findMostOrderedMenuItems();
}