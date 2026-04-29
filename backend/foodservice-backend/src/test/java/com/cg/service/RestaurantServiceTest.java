package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        requestDTO.setRestaurantId(1);
        requestDTO.setRestaurantName("Pizza Palace");
        requestDTO.setRestaurantAddress("MG Road, Delhi");
        requestDTO.setRestaurantPhone("9876543210");
    }

    // =========================================================================
    // addRestaurant()
    // =========================================================================

    @Test
    void addRestaurant_positive() {
        when(restaurantRepository.existsById(1)).thenReturn(false);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponseDTO result = restaurantService.addRestaurant(requestDTO);

        assertEquals("Pizza Palace", result.getRestaurantName());
        assertEquals("MG Road, Delhi", result.getRestaurantAddress());
        assertEquals("9876543210", result.getRestaurantPhone());
    }

    @Test
    void addRestaurant_negative_duplicateId() {
        when(restaurantRepository.existsById(1)).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> restaurantService.addRestaurant(requestDTO));
    }

    // =========================================================================
    // getRestaurantById()
    // =========================================================================

    @Test
    void getRestaurantById_positive() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.getRestaurantById(1);

        assertEquals("Pizza Palace", result.getRestaurantName());
    }

    @Test
    void getRestaurantById_negative() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> restaurantService.getRestaurantById(1));
    }

    // =========================================================================
    // getAllRestaurants()
    // =========================================================================

    @Test
    void getAllRestaurants_positive() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.getAllRestaurants();

        assertEquals(1, result.size());
    }

    @Test
    void getAllRestaurants_negative() {
        when(restaurantRepository.findAll()).thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.getAllRestaurants();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // updateRestaurant()
    // =========================================================================

    @Test
    void updateRestaurant_positive() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO();
        updateDTO.setRestaurantName("Pizza Palace Updated");
        updateDTO.setRestaurantAddress("New Address, Delhi");
        updateDTO.setRestaurantPhone("1111111111");

        RestaurantResponseDTO result = restaurantService.updateRestaurant(1, updateDTO);

        assertEquals("Pizza Palace Updated", result.getRestaurantName());
    }

    @Test
    void updateRestaurant_negative() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> restaurantService.updateRestaurant(1, requestDTO));
    }

    // =========================================================================
    // deleteRestaurant()
    // =========================================================================

    @Test
    void deleteRestaurant_positive() {
        when(restaurantRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> restaurantService.deleteRestaurant(1));
    }

    @Test
    void deleteRestaurant_negative() {
        when(restaurantRepository.existsById(1)).thenReturn(false);

        assertThrows(IdNotFoundException.class,
                () -> restaurantService.deleteRestaurant(1));
    }

    // =========================================================================
    // getRestaurantByName()
    // =========================================================================

    @Test
    void getRestaurantByName_positive() {
        when(restaurantRepository.findByRestaurantName("Pizza Palace"))
                .thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.getRestaurantByName("Pizza Palace");

        assertEquals("Pizza Palace", result.getRestaurantName());
    }

    @Test
    void getRestaurantByName_negative() {
        when(restaurantRepository.findByRestaurantName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(NameNotFoundException.class,
                () -> restaurantService.getRestaurantByName("Unknown"));
    }

    // =========================================================================
    // searchRestaurantsByName()
    // =========================================================================

    @Test
    void searchRestaurantsByName_positive() {
        when(restaurantRepository.findByRestaurantNameContainingIgnoreCase("pizza"))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.searchRestaurantsByName("pizza");

        assertEquals(1, result.size());
        assertEquals("Pizza Palace", result.get(0).getRestaurantName());
    }

    @Test
    void searchRestaurantsByName_negative() {
        when(restaurantRepository.findByRestaurantNameContainingIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.searchRestaurantsByName("xyz");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantByPhone()
    // =========================================================================

    @Test
    void getRestaurantByPhone_positive() {
        when(restaurantRepository.findByRestaurantPhone("9876543210"))
                .thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.getRestaurantByPhone("9876543210");

        assertEquals("9876543210", result.getRestaurantPhone());
    }

    @Test
    void getRestaurantByPhone_negative() {
        when(restaurantRepository.findByRestaurantPhone("0000000000"))
                .thenReturn(Optional.empty());

        assertThrows(PhoneNumberNotFoundException.class,
                () -> restaurantService.getRestaurantByPhone("0000000000"));
    }

    // =========================================================================
    // searchRestaurantsByAddress()
    // =========================================================================

    @Test
    void searchRestaurantsByAddress_positive() {
        when(restaurantRepository.findByRestaurantAddressContainingIgnoreCase("MG Road"))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.searchRestaurantsByAddress("MG Road");

        assertEquals(1, result.size());
    }

    @Test
    void searchRestaurantsByAddress_negative() {
        when(restaurantRepository.findByRestaurantAddressContainingIgnoreCase("Noida"))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.searchRestaurantsByAddress("Noida");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantsHavingMenuItems()
    // =========================================================================

    @Test
    void getRestaurantsHavingMenuItems_positive() {
        when(restaurantRepository.findRestaurantsHavingMenuItems())
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingMenuItems();

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsHavingMenuItems_negative() {
        when(restaurantRepository.findRestaurantsHavingMenuItems())
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingMenuItems();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantByMenuItemId()
    // =========================================================================

    @Test
    void getRestaurantByMenuItemId_positive() {
        when(restaurantRepository.findByMenuItemId(5))
                .thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.getRestaurantByMenuItemId(5);

        assertEquals(1, result.getRestaurantId());
    }

    @Test
    void getRestaurantByMenuItemId_negative() {
        when(restaurantRepository.findByMenuItemId(999))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> restaurantService.getRestaurantByMenuItemId(999));
    }

    // =========================================================================
    // getRestaurantsByMenuItemName()
    // =========================================================================

    @Test
    void getRestaurantsByMenuItemName_positive() {
        when(restaurantRepository.findByMenuItemNameContaining("burger"))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemName("burger");

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsByMenuItemName_negative() {
        when(restaurantRepository.findByMenuItemNameContaining("xyz"))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemName("xyz");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantsByMenuItemPriceRange()
    // =========================================================================

    @Test
    void getRestaurantsByMenuItemPriceRange_positive() {
        when(restaurantRepository.findByMenuItemPriceBetween(50.0, 300.0))
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemPriceRange(50.0, 300.0);

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsByMenuItemPriceRange_negative() {
        when(restaurantRepository.findByMenuItemPriceBetween(5000.0, 9000.0))
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result =
                restaurantService.getRestaurantsByMenuItemPriceRange(5000.0, 9000.0);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantsHavingOrders()
    // =========================================================================

    @Test
    void getRestaurantsHavingOrders_positive() {
        when(restaurantRepository.findRestaurantsHavingOrders())
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingOrders();

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsHavingOrders_negative() {
        when(restaurantRepository.findRestaurantsHavingOrders())
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingOrders();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantByOrderId()
    // =========================================================================

    @Test
    void getRestaurantByOrderId_positive() {
        when(restaurantRepository.findByOrderId(10))
                .thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.getRestaurantByOrderId(10);

        assertEquals(1, result.getRestaurantId());
    }

    @Test
    void getRestaurantByOrderId_negative() {
        when(restaurantRepository.findByOrderId(999))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> restaurantService.getRestaurantByOrderId(999));
    }

    // =========================================================================
    // getRestaurantsHavingRatings()
    // =========================================================================

    @Test
    void getRestaurantsHavingRatings_positive() {
        when(restaurantRepository.findRestaurantsHavingRatings())
                .thenReturn(List.of(restaurant));

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingRatings();

        assertEquals(1, result.size());
    }

    @Test
    void getRestaurantsHavingRatings_negative() {
        when(restaurantRepository.findRestaurantsHavingRatings())
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDTO> result = restaurantService.getRestaurantsHavingRatings();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getRestaurantsByMinAverageRating()
    // =========================================================================

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

    // =========================================================================
    // getAllWithMenuItemCount()
    // =========================================================================

    @Test
    void getAllWithMenuItemCount_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
            new Object[]{1, "Pizza Palace", "MG Road", "9876543210", 5L}
        );

        when(restaurantRepository.findAllWithMenuItemCount()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithMenuItemCount();

        assertEquals(1, result.size());
        assertEquals("Pizza Palace", result.get(0)[1]);
    }

    @Test
    void getAllWithMenuItemCount_negative() {
        when(restaurantRepository.findAllWithMenuItemCount())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = restaurantService.getAllWithMenuItemCount();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getAllWithOrderCount()
    // =========================================================================

    
    @Test
    void getAllWithOrderCount_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
            new Object[]{1, "Pizza Palace", 12L}
        );

        when(restaurantRepository.findAllWithOrderCount()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithOrderCount();

        assertEquals(1, result.size());
        assertEquals(12L, result.get(0)[2]);
    }

    @Test
    void getAllWithOrderCount_negative() {
        when(restaurantRepository.findAllWithOrderCount())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = restaurantService.getAllWithOrderCount();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getAllWithAverageRating()
    // =========================================================================

    @Test
    void getAllWithAverageRating_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
            new Object[]{1, "Pizza Palace", 4.5}
        );

        when(restaurantRepository.findAllWithAverageRating()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithAverageRating();

        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0)[2]);
    }

    @Test
    void getAllWithAverageRating_negative() {
        when(restaurantRepository.findAllWithAverageRating())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = restaurantService.getAllWithAverageRating();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getAllWithFullSummary()
    // =========================================================================

    @Test
    void getAllWithFullSummary_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
            new Object[]{1, "Pizza Palace", "MG Road", "9876543210", 5L, 12L, 4.5}
        );

        when(restaurantRepository.findAllWithFullSummary()).thenReturn(rows);

        List<Object[]> result = restaurantService.getAllWithFullSummary();

        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0)[6]);
    }

    @Test
    void getAllWithFullSummary_negative() {
        when(restaurantRepository.findAllWithFullSummary())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = restaurantService.getAllWithFullSummary();

        assertTrue(result.isEmpty());
    }
}