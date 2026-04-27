package com.cg.repo;

import com.cg.entity.MenuItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemsRepository extends JpaRepository<MenuItems, Integer> {

    // =========================================================================
    // DERIVED QUERIES — based on MenuItems table columns only
    // (itemName, itemDescription, itemPrice)
    // =========================================================================

    // Find by exact item name
    Optional<MenuItems> findByItemName(String itemName);

    // Search by item name keyword — for search bar (case-insensitive)
    List<MenuItems> findByItemNameContainingIgnoreCase(String keyword);

    // Find all items below or equal to a given price
    List<MenuItems> findByItemPriceLessThanEqual(Double price);

    // Find all items above or equal to a given price
    List<MenuItems> findByItemPriceGreaterThanEqual(Double price);

    // Find all items within a price range
    List<MenuItems> findByItemPriceBetween(Double minPrice, Double maxPrice);

    // =========================================================================
    // DERIVED QUERIES — via @ManyToOne Restaurant relationship
    // Uses: MenuItems → restaurant (JoinColumn restaurant_id)
    // =========================================================================

    // Get all menu items belonging to a specific restaurant
    // Uses: MenuItems @ManyToOne → Restaurant → restaurantId
    List<MenuItems> findByRestaurant_RestaurantId(Integer restaurantId);

    // Get all menu items belonging to a restaurant by restaurant name
    // Uses: MenuItems @ManyToOne → Restaurant → restaurantName
    List<MenuItems> findByRestaurant_RestaurantName(String restaurantName);

    // Search menu items by name within a specific restaurant
    // Uses: restaurant_id FK + itemName
    List<MenuItems> findByRestaurant_RestaurantIdAndItemNameContainingIgnoreCase(
            Integer restaurantId, String keyword);

    // Find items within a price range for a specific restaurant
    // Uses: restaurant_id FK + itemPrice range
    List<MenuItems> findByRestaurant_RestaurantIdAndItemPriceBetween(
            Integer restaurantId, Double minPrice, Double maxPrice);

    // Check if a menu item exists for a given restaurant
    // Uses: restaurant_id FK + itemName exact match
    boolean existsByItemNameAndRestaurant_RestaurantId(String itemName, Integer restaurantId);

    // Count total menu items in a restaurant
    // Uses: restaurant_id FK
    int countByRestaurant_RestaurantId(Integer restaurantId);

    // =========================================================================
    // JPQL QUERIES — using entity field names
    // =========================================================================

    // Get all items of a restaurant ordered by price ascending
    // Uses: MenuItems → restaurant.restaurantId
    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice ASC")
    List<MenuItems> findByRestaurantIdOrderByPriceAsc(@Param("restaurantId") Integer restaurantId);

    // Get all items of a restaurant ordered by price descending
    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice DESC")
    List<MenuItems> findByRestaurantIdOrderByPriceDesc(@Param("restaurantId") Integer restaurantId);

    // Get the most expensive item in a restaurant
    // Uses: MenuItems → restaurant.restaurantId + itemPrice
    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice DESC LIMIT 1")
    Optional<MenuItems> findMostExpensiveByRestaurantId(@Param("restaurantId") Integer restaurantId);

    // Get the cheapest item in a restaurant
    @Query("SELECT m FROM MenuItems m WHERE m.restaurant.restaurantId = :restaurantId " +
           "ORDER BY m.itemPrice ASC LIMIT 1")
    Optional<MenuItems> findCheapestByRestaurantId(@Param("restaurantId") Integer restaurantId);

    // ── Via @OneToMany List<OrderItem> relationship ───────────────────────────

    // Find all menu items that have been ordered at least once
    // Uses: MenuItems → List<OrderItem> (OneToMany mappedBy="menuItem")
    @Query("SELECT DISTINCT oi.menuItem FROM OrderItem oi")
    List<MenuItems> findMenuItemsWithOrders();

    // Find menu item linked to a specific OrderItem
    // Uses: OrderItem @ManyToOne → MenuItems
    @Query("SELECT oi.menuItem FROM OrderItem oi WHERE oi.orderItemId = :orderItemId")
    Optional<MenuItems> findByOrderItemId(@Param("orderItemId") Integer orderItemId);

    // =========================================================================
    // NATIVE QUERIES — directly against DB table and column names
    // =========================================================================

    // Get all menu items with their restaurant name
    // Joins: MenuItems JOIN Restaurants ON restaurant_id
    @Query(value = """
            SELECT m.item_id, m.item_name, m.item_description,
                   m.item_price, r.restaurant_id, r.restaurant_name
            FROM MenuItems m
            JOIN Restaurants r ON m.restaurant_id = r.restaurant_id
            WHERE m.restaurant_id = :restaurantId
            """, nativeQuery = true)
    List<Object[]> findMenuItemsWithRestaurantByRestaurantId(
            @Param("restaurantId") Integer restaurantId);

    // Get average price of menu items per restaurant
    // Joins: MenuItems JOIN Restaurants ON restaurant_id
    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   AVG(m.item_price) AS avg_price
            FROM Restaurants r
            JOIN MenuItems m ON r.restaurant_id = m.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAveragePricePerRestaurant();

    // Get each menu item with how many times it has been ordered
    // Joins: MenuItems LEFT JOIN OrderItems ON item_id
    @Query(value = """
            SELECT m.item_id, m.item_name, m.item_price,
                   COUNT(oi.order_item_id) AS times_ordered
            FROM MenuItems m
            LEFT JOIN OrderItems oi ON m.item_id = oi.item_id
            GROUP BY m.item_id, m.item_name, m.item_price
            ORDER BY times_ordered DESC
            """, nativeQuery = true)
    List<Object[]> findMenuItemsWithOrderCount();

    // Get the most ordered menu items across all restaurants
    // Joins: MenuItems JOIN OrderItems JOIN Restaurants
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