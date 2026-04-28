package com.cg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Rating addRating(Rating rating) {
    	
        if (ratingRepository.findByOrder_OrderId(
                rating.getOrder().getOrderId()).isPresent()) {
            throw new DuplicateDataException("Order already rated");
        }

        // Validate rating range
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new InvalidInputException("Rating must be between 1 and 5");
        }

        // Fetch and attach proper entities
        Order order = orderRepository.findById(
                rating.getOrder().getOrderId())
                .orElseThrow(() -> new IdNotFoundException("Order not found"));

        rating.setOrder(order);

        rating.setRestaurant(
                restaurantRepository.findById(
                        rating.getRestaurant().getRestaurantId())
                        .orElseThrow(() -> new IdNotFoundException("Restaurant not found"))
        );

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
    public Rating updateRating(Integer ratingId, Rating rating) {

        Rating existing = getRatingById(ratingId);

        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new InvalidInputException("Rating must be between 1 and 5");
        }

        existing.setRating(rating.getRating());
        existing.setReview(rating.getReview());

        return ratingRepository.save(existing);
    }

    @Override
    public void deleteRating(Integer ratingId) {
        Rating rating = getRatingById(ratingId);
        ratingRepository.delete(rating);
    }
}