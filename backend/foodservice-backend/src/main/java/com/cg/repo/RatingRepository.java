package com.cg.repo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    // Get all ratings for a restaurant
    List<Rating> findByRestaurant_RestaurantId(Integer restaurantId);

    // Get rating by order
    Optional<Rating> findByOrder_OrderId(Integer orderId);
}