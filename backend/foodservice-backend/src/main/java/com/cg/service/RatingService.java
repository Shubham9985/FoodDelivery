package com.cg.service;

import java.util.List;

import com.cg.dto.RatingDTO;
import com.cg.entity.Rating;

public interface RatingService {

    Rating addRating(RatingDTO ratingdto);

    Rating getRatingById(Integer ratingId);

    List<Rating> getRatingsByRestaurant(Integer restaurantId);

    Rating updateRating(Integer ratingId, RatingDTO rating);

    void deleteRating(Integer ratingId);
}