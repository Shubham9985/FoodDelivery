package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.cg.exceptions.NameNotFoundException;
import com.cg.dto.MenuItemsDTO;
import com.cg.entity.MenuItems;
import com.cg.entity.Restaurant;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.MenuItemsRepository;
import com.cg.repo.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuItemsServiceTest {

    @Mock
    private MenuItemsRepository menuItemsRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemsServiceImpl menuItemsService;

    private Restaurant restaurant;
    private MenuItems menuItem;
    private MenuItemsDTO.Request requestDTO;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        restaurant.setRestaurantName("Pizza Palace");

        menuItem = new MenuItems();
        menuItem.setItemId(10);
        menuItem.setItemName("Margherita Pizza");
        menuItem.setItemDescription("Classic");
        menuItem.setItemPrice(new BigDecimal("299"));
        menuItem.setRestaurant(restaurant);

        requestDTO = new MenuItemsDTO.Request();
        requestDTO.setItemName("Margherita Pizza");
        requestDTO.setItemDescription("Classic");
        requestDTO.setItemPrice(new BigDecimal("299"));
        requestDTO.setRestaurantId(1);
    }

    // ================= add =================
    @Test
    void addMenuItem_positive() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.save(any(MenuItems.class))).thenReturn(menuItem);

        MenuItemsDTO.Response result = menuItemsService.addMenuItem(requestDTO);

        assertEquals(new BigDecimal("299"), result.getItemPrice());
    }

    // ================= update =================
    @Test
    void updateMenuItem_positive() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.of(menuItem));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.save(any(MenuItems.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        requestDTO.setItemPrice(new BigDecimal("350"));

        MenuItemsDTO.Response result = menuItemsService.updateMenuItem(10, requestDTO);

        assertEquals(new BigDecimal("350"), result.getItemPrice());
    }

    // ================= price max =================
    @Test
    void getMenuItemsByMaxPrice_positive() {
        when(menuItemsRepository.findByItemPriceLessThanEqual(new BigDecimal("300")))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByMaxPrice(new BigDecimal("300"));

        assertEquals(1, result.size());
    }

    // ================= price min =================
    @Test
    void getMenuItemsByMinPrice_positive() {
        when(menuItemsRepository.findByItemPriceGreaterThanEqual(new BigDecimal("299")))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByMinPrice(new BigDecimal("299"));

        assertEquals(1, result.size());
    }

    // ================= price range =================
    @Test
    void getMenuItemsByPriceRange_positive() {
        when(menuItemsRepository.findByItemPriceBetween(
                new BigDecimal("100"), new BigDecimal("400")))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRange(
                        new BigDecimal("100"), new BigDecimal("400"));

        assertEquals(1, result.size());
    }

    // ================= restaurant price range =================
    @Test
    void getMenuItemsByPriceRangeInRestaurant_positive() {
        when(menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemPriceBetween(
                        eq(1),
                        eq(new BigDecimal("100")),
                        eq(new BigDecimal("400"))))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(
                        1,
                        new BigDecimal("100"),
                        new BigDecimal("400"));

        assertEquals(1, result.size());
    }

    // ================= Object[] test =================
    @Test
    void getMenuItemsWithRestaurant_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
                new Object[]{10, "Pizza", "Classic", new BigDecimal("299"), 1, "Pizza Palace"}
        );

        when(menuItemsRepository.findMenuItemsWithRestaurantByRestaurantId(1))
                .thenReturn(rows);

        List<Object[]> result =
                menuItemsService.getMenuItemsWithRestaurantByRestaurantId(1);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("299"), result.get(0)[3]);
    }

    // ================= aggregate =================
    @Test
    void getAveragePricePerRestaurant_positive() {
        List<Object[]> rows = java.util.Collections.singletonList(
                new Object[]{1, "Pizza Palace", new BigDecimal("250")}
        );

        when(menuItemsRepository.findAveragePricePerRestaurant())
                .thenReturn(rows);

        List<Object[]> result =
                menuItemsService.getAveragePricePerRestaurant();

        assertEquals(new BigDecimal("250"), result.get(0)[2]);
    }

    // ================= negative =================
    @Test
    void getMenuItemByName_negative() {
        when(menuItemsRepository.findByItemName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(NameNotFoundException.class,
                () -> menuItemsService.getMenuItemByName("Unknown"));
    }
}