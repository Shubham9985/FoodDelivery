package com.cg.repo;

import com.cg.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Optional<Restaurant> findByRestaurantName(String restaurantName);

    List<Restaurant> findByRestaurantNameContainingIgnoreCase(String keyword);

    Optional<Restaurant> findByRestaurantPhone(String restaurantPhone);

    List<Restaurant> findByRestaurantAddressContainingIgnoreCase(String address);

    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m")
    List<Restaurant> findRestaurantsHavingMenuItems();

    @Query("SELECT m.restaurant FROM MenuItems m WHERE m.itemId = :itemId")
    Optional<Restaurant> findByMenuItemId(@Param("itemId") int itemId);

    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m " +
           "WHERE LOWER(m.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> findByMenuItemNameContaining(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT m.restaurant FROM MenuItems m " +
           "WHERE m.itemPrice BETWEEN :minPrice AND :maxPrice")
    List<Restaurant> findByMenuItemPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                                @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT DISTINCT o.restaurant FROM Order o")
    List<Restaurant> findRestaurantsHavingOrders();

    @Query("SELECT o.restaurant FROM Order o WHERE o.orderId = :orderId")
    Optional<Restaurant> findByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT DISTINCT r.restaurant FROM Rating r")
    List<Restaurant> findRestaurantsHavingRatings();

    @Query("SELECT r.restaurant FROM Rating r " +
           "GROUP BY r.restaurant " +
           "HAVING AVG(r.rating) >= :minRating")
    List<Restaurant> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating);

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

    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   COUNT(o.order_id) AS total_orders
            FROM Restaurants r
            LEFT JOIN Orders o ON r.restaurant_id = o.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAllWithOrderCount();

    @Query(value = """
            SELECT r.restaurant_id, r.restaurant_name,
                   AVG(rt.rating) AS avg_rating
            FROM Restaurants r
            LEFT JOIN Ratings rt ON r.restaurant_id = rt.restaurant_id
            GROUP BY r.restaurant_id, r.restaurant_name
            """, nativeQuery = true)
    List<Object[]> findAllWithAverageRating();

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