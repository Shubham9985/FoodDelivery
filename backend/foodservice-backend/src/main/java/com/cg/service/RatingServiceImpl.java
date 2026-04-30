package com.cg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.RatingDTO;
import com.cg.entity.Order;
import com.cg.entity.Rating;
import com.cg.entity.Restaurant;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.OrderRepository;
import com.cg.repo.RatingRepository;
import com.cg.repo.RestaurantRepository;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    // 🔄 Mapping
    private Rating mapToEntity(RatingDTO dto) {

        Rating r = new Rating();
        r.setRating(dto.getRating());
        r.setReview(dto.getReview());

        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() -> new IdNotFoundException("Order not found"));

        Restaurant restaurant = restaurantRepo.findById(dto.getRestaurantId())
                .orElseThrow(() -> new IdNotFoundException("Restaurant not found"));

        r.setOrder(order);
        r.setRestaurant(restaurant);

        return r;
    }

    private RatingDTO mapToDTO(Rating r) {
        return new RatingDTO(
                r.getRatingId(),
                r.getRating(),
                r.getReview(),
                r.getOrder().getOrderId(),
                r.getRestaurant().getRestaurantId()
        );
    }

    // ➕ Add
    @Override
    public RatingDTO addRating(RatingDTO dto) {

        if (ratingRepo.existsByOrderOrderId(dto.getOrderId())) {
            throw new DuplicateDataException("Order already rated");
        }

        Rating saved = ratingRepo.save(mapToEntity(dto));
        return mapToDTO(saved);
    }

    // 🔍 Get by ID
    @Override
    public RatingDTO getRatingById(Integer id) {
        Rating r = ratingRepo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Rating not found"));
        return mapToDTO(r);
    }

    // 📄 Get All
    @Override
    public List<RatingDTO> getAllRatings() {
        return ratingRepo.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 🍽️ Get by Restaurant
    @Override
    public List<RatingDTO> getRatingsByRestaurant(Integer restaurantId) {
        return ratingRepo.findByRestaurantRestaurantId(restaurantId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✏️ Update
    @Override
    public RatingDTO updateRating(Integer id, RatingDTO dto) {

        Rating existing = ratingRepo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Rating not found"));

        existing.setRating(dto.getRating());
        existing.setReview(dto.getReview());

        return mapToDTO(ratingRepo.save(existing));
    }

    // ❌ Delete
    @Override
    public void deleteRating(Integer id) {
        Rating r = ratingRepo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Rating not found"));
        ratingRepo.delete(r);
    }

    // ⭐ Average Rating
    @Override
    public Double getAverageRating(Integer restaurantId) {

        List<Rating> ratings = ratingRepo.findByRestaurantRestaurantId(restaurantId);

        if (ratings.isEmpty()) {
			return 0.0;
		}

        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }
}