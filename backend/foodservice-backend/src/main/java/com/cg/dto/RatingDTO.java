package com.cg.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RatingDTO {

	private Integer ratingId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 300)
    private String review;

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer restaurantId;

    public RatingDTO() {}

    public RatingDTO(Integer ratingId, Integer rating, String review,
                     Integer orderId, Integer restaurantId) {
        this.ratingId = ratingId;
        this.rating = rating;
        this.review = review;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
    }
    
    
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

    // getters & setters
    
    
}