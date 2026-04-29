package com.cg.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cg.dto.RatingDTO;
import com.cg.entity.Order;
import com.cg.entity.Rating;
import com.cg.entity.Restaurant;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.OrderRepository;
import com.cg.repo.RatingRepository;
import com.cg.repo.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepo;

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private RestaurantRepository restaurantRepo;

    @InjectMocks
    private RatingServiceImpl service;

    private Rating rating;
    private Order order;
    private Restaurant restaurant;

    @BeforeEach
    void setup() {

        order = new Order();
        order.setOrderId(1);

        restaurant = new Restaurant();
        restaurant.setRestaurantId(10);

        rating = new Rating();
        rating.setRatingId(1);
        rating.setRating(5);
        rating.setOrder(order);
        rating.setRestaurant(restaurant);
    }

    @Test
    void testAddRatingSuccess() {

        when(orderRepo.findById(1)).thenReturn(Optional.of(order));
        when(restaurantRepo.findById(10)).thenReturn(Optional.of(restaurant));
        when(ratingRepo.existsByOrderOrderId(1)).thenReturn(false);
        when(ratingRepo.save(any())).thenReturn(rating);

        RatingDTO dto = new RatingDTO(null, 5, "Good", 1, 10);

        RatingDTO result = service.addRating(dto);

        assertEquals(5, result.getRating());
    }

    @Test
    void testDuplicateRating() {

        when(ratingRepo.existsByOrderOrderId(1)).thenReturn(true);

        RatingDTO dto = new RatingDTO(null, 5, "Good", 1, 10);

        assertThrows(DuplicateDataException.class,
                () -> service.addRating(dto));
    }

    @Test
    void testGetRatingNotFound() {

        when(ratingRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.getRatingById(1));
    }

    @Test
    void testAverageRating() {

        when(ratingRepo.findByRestaurantRestaurantId(10))
                .thenReturn(List.of(rating));

        assertEquals(5.0, service.getAverageRating(10));
    }
}