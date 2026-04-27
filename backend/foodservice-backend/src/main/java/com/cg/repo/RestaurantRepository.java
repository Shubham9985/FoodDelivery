package com.cg.repo;


import com.cg.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    // =========================================================================
    // DERIVED QUERIES — based on Restaurant table columns only
    // (restaurantName, restaurantAddress, restaurantPhone)
    // =========================================================================

    // Find by exact restaurant name
    Optional<Restaurant> findByRestaurantName(String restaurantName);

    // Search by restaurant name — for search bar (case-insensitive)
    List<Restaurant> findByRestaurantNameContainingIgnoreCase(String keyword);

    // Find by exact phone number
    Optional<Restaurant> findByRestaurantPhone(String restaurantPhone);

    // Search by address keyword
    List<Restaurant> findByRestaurantAddressContainingIgnoreCase(String address);

    // =========================================================================
    // JPQL QUERIES — using entity relationships
    // =========================================================================

    // ── Via List<MenuItems> relationship ──────────────────────────────────────

    // Get all restaurants that have at least one menu item
    // Uses: Restaurant → List<MenuItems> (OneToMany mappedBy="restaurant")
    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m")
    List<Restaurant> findRestaurantsHavingMenuItems();

    // Find the restaurant that owns a specific menu item by itemId
    // Uses: MenuItems @ManyToOne → Restaurant
    @Query("SELECT m.restaurant FROM MenuItems m WHERE m.itemId = :itemId")
    Optional<Restaurant> findByMenuItemId(@Param("itemId") int itemId);

    // Find restaurants that have a menu item matching a name keyword
    // Uses: Restaurant → List<MenuItems>
    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m " +
           "WHERE LOWER(m.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> findByMenuItemNameContaining(@Param("keyword") String keyword);

    // Find restaurants that have any menu item within a price range
    // Uses: Restaurant → List<MenuItems> → itemPrice
    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m " +
           "WHERE m.itemPrice BETWEEN :minPrice AND :maxPrice")
    List<Restaurant> findByMenuItemPriceBetween(@Param("minPrice") Double minPrice,
                                                @Param("maxPrice") Double maxPrice);

    // ── Via List<Order> relationship ──────────────────────────────────────────

    // Get all restaurants that have received at least one order
    // Uses: Restaurant → List<Order> (OneToMany mappedBy="restaurant")
    @Query("SELECT DISTINCT o.restaurant FROM Order o")
    List<Restaurant> findRestaurantsHavingOrders();

    // Find the restaurant linked to a specific order
    // Uses: Order @ManyToOne → Restaurant
    @Query("SELECT o.restaurant FROM Order o WHERE o.orderId = :orderId")
    Optional<Restaurant> findByOrderId(@Param("orderId") Integer orderId);

    // ── Via List<Rating> relationship ─────────────────────────────────────────

    // Get all restaurants that have at least one rating
    // Uses: Restaurant → List<Rating> (OneToMany mappedBy="restaurant")
    @Query("SELECT DISTINCT r.restaurant FROM Rating r")
    List<Restaurant> findRestaurantsHavingRatings();

    // Find restaurants with average rating above a given value
    // Uses: Restaurant → List<Rating> → rating field
    @Query("SELECT r.restaurant FROM Rating r " +
           "GROUP BY r.restaurant " +
           "HAVING AVG(r.rating) >= :minRating")
    List<Restaurant> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating);

    // =========================================================================
    // NATIVE QUERIES — directly against DB table and column names
    // =========================================================================

    // Get every restaurant with its total menu item count
    // Joins: Restaurants LEFT JOIN MenuItems ON restaurant_id
    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   r.restaurant_address, r.restaurant_phone,
                   COUNT(m.item_id) AS total_items
            FROM Restaurants r
            LEFT JOIN MenuItems m ON r.restaurant_id = m.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name,
                     r.restaurant_address, r.restaurant_phone
            """, nativeQuery = true)
    List<Object[]> findAllWithMenuItemCount();

    // Get every restaurant with its total order count
    // Joins: Restaurants LEFT JOIN Orders ON restaurant_id
    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   COUNT(o.order_id) AS total_orders
            FROM Restaurants r
            LEFT JOIN Orders o ON r.restaurant_id = o.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAllWithOrderCount();

    // Get every restaurant with its average rating
    // Joins: Restaurants LEFT JOIN Ratings ON restaurant_id
    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   AVG(rt.rating) AS avg_rating
            FROM Restaurants r
            LEFT JOIN Ratings rt ON r.restaurant_id = rt.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAllWithAverageRating();

    // Full summary — restaurant with menu count, order count, avg rating
    // Joins: Restaurants + MenuItems + Orders + Ratings
    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   r.restaurant_address, r.restaurant_phone,
                   COUNT(DISTINCT m.item_id)  AS total_menu_items,
                   COUNT(DISTINCT o.order_id) AS total_orders,
                   AVG(rt.rating)             AS avg_rating
            FROM Restaurants r
            LEFT JOIN MenuItems m  ON r.restaurant_id = m.restaurant_id
            LEFT JOIN Orders o     ON r.restaurant_id = o.restaurant_id
            LEFT JOIN Ratings rt   ON r.restaurant_id = rt.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name,
                     r.restaurant_address, r.restaurant_phone
            """, nativeQuery = true)
    List<Object[]> findAllWithFullSummary();
}
