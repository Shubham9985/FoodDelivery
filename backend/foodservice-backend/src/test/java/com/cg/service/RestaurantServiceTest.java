package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.cg.exceptions.PhoneNumberNotFoundException;
import com.cg.exceptions.NameNotFoundException;
import com.cg.dto.RestaurantRequestDTO;
import com.cg.dto.RestaurantResponseDTO;
import com.cg.entity.Restaurant;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;
    private RestaurantRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        restaurant.setRestaurantName("Pizza Palace");
        restaurant.setRestaurantAddress("MG Road, Delhi");
        restaurant.setRestaurantPhone("9876543210");

        requestDTO = new RestaurantRequestDTO();
        requestDTO.setRestaurantName("Pizza Palace");
        requestDTO.setRestaurantAddress("MG Road, Delhi");
        requestDTO.setRestaurantPhone("9876543210");
    }

    // ================= PRICE RANGE =================
    @Test
    void getRestaurantsByMenuItemPriceRange_positive() {
        when(restaurantRepository.findByMenuItemPriceBetween(
                new BigDecimal("50"), new BigDecimal("300")))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemPriceRange(
                        new BigDecimal("50"), new BigDecimal("300"));

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsByMenuItemPriceRange_negative() {
        when(restaurantRepository.findByMenuItemPriceBetween(
                new BigDecimal("5000"), new BigDecimal("9000")))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemPriceRange(
                        new BigDecimal("5000"), new BigDecimal("9000"));

        assertTrue(result.isEmpty());
    }

    // ================= RATING =================
 // ================= RATING =================
    @Test
    void getRestaurantsByMinAverageRating_positive() {
        when(restaurantRepository.findByAverageRatingGreaterThanEqual(4.0))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMinAverageRating(4.0);

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsByMinAverageRating_negative() {
        when(restaurantRepository.findByAverageRatingGreaterThanEqual(5.0))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMinAverageRating(5.0);

        assertTrue(result.isEmpty());
    }
    // ================= AGGREGATE =================
    @Test
    void getAllWithAverageRating_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
                new Object[]{1, "Pizza Palace", new BigDecimal("4.5")}
        );

        when(restaurantRepository.findAllWithAverageRating()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithAverageRating();

        assertEquals(new BigDecimal("4.5"), result.get(0)[2]);
    }

    @Test
    void getAllWithFullSummary_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
                new Object[]{1, "Pizza Palace", "MG Road", "9876543210",
                        5L, 12L, new BigDecimal("4.5")}
        );

        when(restaurantRepository.findAllWithFullSummary()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithFullSummary();

        assertEquals(new BigDecimal("4.5"), result.get(0)[6]);
    }
}