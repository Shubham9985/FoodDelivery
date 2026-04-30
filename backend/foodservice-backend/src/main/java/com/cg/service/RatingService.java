package com.cg.service;

import java.util.List;

import com.cg.dto.RatingDTO;

public interface RatingService {

	RatingDTO addRating(RatingDTO dto);

    RatingDTO getRatingById(Integer id);

    List<RatingDTO> getAllRatings();

    List<RatingDTO> getRatingsByRestaurant(Integer restaurantId);

    RatingDTO updateRating(Integer id, RatingDTO dto);

    void deleteRating(Integer id);

    Double getAverageRating(Integer restaurantId);
}