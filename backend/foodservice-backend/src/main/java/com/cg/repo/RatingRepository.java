package com.cg.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    // Get all ratings for a restaurant
	boolean existsByOrderOrderId(Integer orderId);

    List<Rating> findByRestaurantRestaurantId(Integer restaurantId);
}