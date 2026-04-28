package com.cg.service;

import java.util.List;

import com.cg.entity.Rating;

public interface RatingService {

    Rating addRating(Rating rating);

    Rating getRatingById(Integer ratingId);

    List<Rating> getRatingsByRestaurant(Integer restaurantId);

    Rating updateRating(Integer ratingId, Rating rating);

    void deleteRating(Integer ratingId);
}