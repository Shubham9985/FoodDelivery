package com.cg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.RatingDTO;
import com.cg.entity.Order;
import com.cg.entity.Rating;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.exceptions.InvalidInputException;
import com.cg.repo.OrderRepository;
import com.cg.repo.RatingRepository;
import com.cg.repo.RestaurantRepository;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 🔁 DTO → Entity
    private Rating mapToEntity(RatingDTO dto, Order order, com.cg.entity.Restaurant restaurant) {
        Rating rating = new Rating();
        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());
        rating.setOrder(order);
        rating.setRestaurant(restaurant);
        return rating;
    }

    @Override
    public Rating addRating(RatingDTO dto) {

        // ❌ FIX: use dto.getOrderId() instead of dto.getOrder()
        if (ratingRepository.findByOrder_OrderId(dto.getOrderId()).isPresent()) {
            throw new DuplicateDataException("Order already rated");
        }

        // Validate rating
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new InvalidInputException("Rating must be between 1 and 5");
        }

        // Fetch Order
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IdNotFoundException("Order not found"));

        // Fetch Restaurant
        com.cg.entity.Restaurant restaurant = restaurantRepository
                .findById(dto.getRestaurantId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant not found"));

        // 🔁 Map DTO → Entity
        Rating rating = mapToEntity(dto, order, restaurant);

        return ratingRepository.save(rating);
    }

    @Override
    public Rating getRatingById(Integer ratingId) {
        return ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IdNotFoundException("Rating not found"));
    }

    @Override
    public List<Rating> getRatingsByRestaurant(Integer restaurantId) {
        return ratingRepository.findByRestaurant_RestaurantId(restaurantId);
    }

    @Override
    public Rating updateRating(Integer ratingId, RatingDTO dto) {

        Rating existing = getRatingById(ratingId);

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new InvalidInputException("Rating must be between 1 and 5");
        }

        existing.setRating(dto.getRating());
        existing.setReview(dto.getReview());

        return ratingRepository.save(existing);
    }

    @Override
    public void deleteRating(Integer ratingId) {
        Rating rating = getRatingById(ratingId);
        ratingRepository.delete(rating);
    }
}