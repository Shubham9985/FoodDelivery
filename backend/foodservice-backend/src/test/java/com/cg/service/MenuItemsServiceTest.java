package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        restaurant.setRestaurantAddress("MG Road, Delhi");
        restaurant.setRestaurantPhone("9876543210");

        menuItem = new MenuItems();
        menuItem.setItemId(10);
        menuItem.setItemName("Margherita Pizza");
        menuItem.setItemDescription("Classic tomato and cheese pizza");
        menuItem.setItemPrice(299.0);
        menuItem.setRestaurant(restaurant);

        requestDTO = new MenuItemsDTO.Request();
        requestDTO.setItemName("Margherita Pizza");
        requestDTO.setItemDescription("Classic tomato and cheese pizza");
        requestDTO.setItemPrice(299.0);
        requestDTO.setRestaurantId(1);
    }

    // =========================================================================
    // addMenuItem()
    // =========================================================================

    @Test
    void addMenuItem_positive() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.save(any(MenuItems.class))).thenReturn(menuItem);

        MenuItemsDTO.Response result = menuItemsService.addMenuItem(requestDTO);

        assertEquals("Margherita Pizza", result.getItemName());
        assertEquals(299.0, result.getItemPrice());
        assertEquals(1, result.getRestaurantId());
    }

    @Test
    void addMenuItem_negative_restaurantNotFound() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.addMenuItem(requestDTO));
    }

    // =========================================================================
    // getMenuItemById()
    // =========================================================================

    @Test
    void getMenuItemById_positive() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.of(menuItem));

        MenuItemsDTO.Response result = menuItemsService.getMenuItemById(10);

        assertEquals("Margherita Pizza", result.getItemName());
    }

    @Test
    void getMenuItemById_negative() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.getMenuItemById(10));
    }

    // =========================================================================
    // getAllMenuItems()
    // =========================================================================

    @Test
    void getAllMenuItems_positive() {
        when(menuItemsRepository.findAll()).thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.getAllMenuItems();

        assertEquals(1, result.size());
    }

    @Test
    void getAllMenuItems_negative() {
        when(menuItemsRepository.findAll()).thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.getAllMenuItems();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // updateMenuItem()
    // =========================================================================

    @Test
    void updateMenuItem_positive() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.of(menuItem));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.save(any(MenuItems.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemsDTO.Request updateDTO = new MenuItemsDTO.Request();
        updateDTO.setItemName("Pepperoni Pizza");
        updateDTO.setItemDescription("Spicy pepperoni");
        updateDTO.setItemPrice(399.0);
        updateDTO.setRestaurantId(1);

        MenuItemsDTO.Response result = menuItemsService.updateMenuItem(10, updateDTO);

        assertEquals("Pepperoni Pizza", result.getItemName());
        assertNotNull(result);
    }

    @Test
    void updateMenuItem_negative_itemNotFound() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.updateMenuItem(10, requestDTO));
    }

    @Test
    void updateMenuItem_negative_restaurantNotFound() {
        when(menuItemsRepository.findById(10)).thenReturn(Optional.of(menuItem));
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.updateMenuItem(10, requestDTO));
    }

    // =========================================================================
    // deleteMenuItem()
    // =========================================================================

    @Test
    void deleteMenuItem_positive() {
        when(menuItemsRepository.existsById(10)).thenReturn(true);

        assertDoesNotThrow(() -> menuItemsService.deleteMenuItem(10));
    }

    @Test
    void deleteMenuItem_negative() {
        when(menuItemsRepository.existsById(10)).thenReturn(false);

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.deleteMenuItem(10));
    }

    // =========================================================================
    // getMenuItemByName()
    // =========================================================================

    @Test
    void getMenuItemByName_positive() {
        when(menuItemsRepository.findByItemName("Margherita Pizza"))
                .thenReturn(Optional.of(menuItem));

        MenuItemsDTO.Response result = menuItemsService.getMenuItemByName("Margherita Pizza");

        assertEquals("Margherita Pizza", result.getItemName());
    }

    @Test
    void getMenuItemByName_negative() {
        when(menuItemsRepository.findByItemName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(NameNotFoundException.class,
                () -> menuItemsService.getMenuItemByName("Unknown"));
    }

    // =========================================================================
    // searchMenuItemsByName()
    // =========================================================================

    @Test
    void searchMenuItemsByName_positive() {
        when(menuItemsRepository.findByItemNameContainingIgnoreCase("pizza"))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.searchMenuItemsByName("pizza");

        assertEquals(1, result.size());
        assertEquals("Margherita Pizza", result.get(0).getItemName());
    }

    @Test
    void searchMenuItemsByName_negative() {
        when(menuItemsRepository.findByItemNameContainingIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.searchMenuItemsByName("xyz");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByMaxPrice()
    // =========================================================================

    @Test
    void getMenuItemsByMaxPrice_positive() {
        when(menuItemsRepository.findByItemPriceLessThanEqual(300.0))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByMaxPrice(300.0);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByMaxPrice_negative() {
        when(menuItemsRepository.findByItemPriceLessThanEqual(10.0))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByMaxPrice(10.0);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByMinPrice()
    // =========================================================================

    @Test
    void getMenuItemsByMinPrice_positive() {
        when(menuItemsRepository.findByItemPriceGreaterThanEqual(200.0))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByMinPrice(200.0);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByMinPrice_negative() {
        when(menuItemsRepository.findByItemPriceGreaterThanEqual(5000.0))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByMinPrice(5000.0);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByPriceRange()
    // =========================================================================

    @Test
    void getMenuItemsByPriceRange_positive() {
        when(menuItemsRepository.findByItemPriceBetween(100.0, 400.0))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRange(100.0, 400.0);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByPriceRange_negative() {
        when(menuItemsRepository.findByItemPriceBetween(1000.0, 2000.0))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRange(1000.0, 2000.0);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByRestaurantId()
    // =========================================================================

    @Test
    void getMenuItemsByRestaurantId_positive() {
        when(menuItemsRepository.findByRestaurant_RestaurantId(1))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByRestaurantId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRestaurantId());
    }

    @Test
    void getMenuItemsByRestaurantId_negative() {
        when(menuItemsRepository.findByRestaurant_RestaurantId(99))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsByRestaurantId(99);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByRestaurantName()
    // =========================================================================

    @Test
    void getMenuItemsByRestaurantName_positive() {
        when(menuItemsRepository.findByRestaurant_RestaurantName("Pizza Palace"))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantName("Pizza Palace");

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByRestaurantName_negative() {
        when(menuItemsRepository.findByRestaurant_RestaurantName("Unknown"))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantName("Unknown");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // searchMenuItemsByNameInRestaurant()
    // =========================================================================

    @Test
    void searchMenuItemsByNameInRestaurant_positive() {
        when(menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemNameContainingIgnoreCase(1, "pizza"))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.searchMenuItemsByNameInRestaurant(1, "pizza");

        assertEquals(1, result.size());
    }

    @Test
    void searchMenuItemsByNameInRestaurant_negative() {
        when(menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemNameContainingIgnoreCase(1, "xyz"))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.searchMenuItemsByNameInRestaurant(1, "xyz");

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByPriceRangeInRestaurant()
    // =========================================================================

    @Test
    void getMenuItemsByPriceRangeInRestaurant_positive() {
        when(menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemPriceBetween(1, 100.0, 400.0))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(1, 100.0, 400.0);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByPriceRangeInRestaurant_negative() {
        when(menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemPriceBetween(1, 5000.0, 9000.0))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByPriceRangeInRestaurant(1, 5000.0, 9000.0);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // checkMenuItemExistsInRestaurant()
    // =========================================================================

    @Test
    void checkMenuItemExistsInRestaurant_positive() {
        when(menuItemsRepository
                .existsByItemNameAndRestaurant_RestaurantId("Margherita Pizza", 1))
                .thenReturn(true);

        boolean result = menuItemsService.checkMenuItemExistsInRestaurant("Margherita Pizza", 1);

        assertTrue(result);
    }

    @Test
    void checkMenuItemExistsInRestaurant_negative() {
        when(menuItemsRepository
                .existsByItemNameAndRestaurant_RestaurantId("Unknown Item", 1))
                .thenReturn(false);

        boolean result = menuItemsService.checkMenuItemExistsInRestaurant("Unknown Item", 1);

        assertFalse(result);
    }

    // =========================================================================
    // countMenuItemsByRestaurant()
    // =========================================================================

    @Test
    void countMenuItemsByRestaurant_positive() {
        when(menuItemsRepository.countByRestaurant_RestaurantId(1)).thenReturn(5);

        int result = menuItemsService.countMenuItemsByRestaurant(1);

        assertEquals(5, result);
    }

    @Test
    void countMenuItemsByRestaurant_negative() {
        when(menuItemsRepository.countByRestaurant_RestaurantId(99)).thenReturn(0);

        int result = menuItemsService.countMenuItemsByRestaurant(99);

        assertEquals(0, result);
    }

    // =========================================================================
    // getMenuItemsByRestaurantIdOrderByPriceAsc()
    // =========================================================================

    @Test
    void getMenuItemsByRestaurantIdOrderByPriceAsc_positive() {
        when(menuItemsRepository.findByRestaurantIdOrderByPriceAsc(1))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantIdOrderByPriceAsc(1);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByRestaurantIdOrderByPriceAsc_negative() {
        when(menuItemsRepository.findByRestaurantIdOrderByPriceAsc(99))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantIdOrderByPriceAsc(99);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsByRestaurantIdOrderByPriceDesc()
    // =========================================================================

    @Test
    void getMenuItemsByRestaurantIdOrderByPriceDesc_positive() {
        when(menuItemsRepository.findByRestaurantIdOrderByPriceDesc(1))
                .thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantIdOrderByPriceDesc(1);

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsByRestaurantIdOrderByPriceDesc_negative() {
        when(menuItemsRepository.findByRestaurantIdOrderByPriceDesc(99))
                .thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result =
                menuItemsService.getMenuItemsByRestaurantIdOrderByPriceDesc(99);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMostExpensiveMenuItemByRestaurant()
    // =========================================================================

    @Test
    void getMostExpensiveMenuItemByRestaurant_positive() {
        when(menuItemsRepository.findMostExpensiveByRestaurantId(1))
                .thenReturn(Optional.of(menuItem));

        MenuItemsDTO.Response result =
                menuItemsService.getMostExpensiveMenuItemByRestaurant(1);

        assertEquals("Margherita Pizza", result.getItemName());
    }

    @Test
    void getMostExpensiveMenuItemByRestaurant_negative() {
        when(menuItemsRepository.findMostExpensiveByRestaurantId(99))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.getMostExpensiveMenuItemByRestaurant(99));
    }

    // =========================================================================
    // getCheapestMenuItemByRestaurant()
    // =========================================================================

    @Test
    void getCheapestMenuItemByRestaurant_positive() {
        when(menuItemsRepository.findCheapestByRestaurantId(1))
                .thenReturn(Optional.of(menuItem));

        MenuItemsDTO.Response result =
                menuItemsService.getCheapestMenuItemByRestaurant(1);

        assertEquals("Margherita Pizza", result.getItemName());
    }

    @Test
    void getCheapestMenuItemByRestaurant_negative() {
        when(menuItemsRepository.findCheapestByRestaurantId(99))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.getCheapestMenuItemByRestaurant(99));
    }

    // =========================================================================
    // getMenuItemsWithOrders()
    // =========================================================================

    @Test
    void getMenuItemsWithOrders_positive() {
        when(menuItemsRepository.findMenuItemsWithOrders()).thenReturn(List.of(menuItem));

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsWithOrders();

        assertEquals(1, result.size());
    }

    @Test
    void getMenuItemsWithOrders_negative() {
        when(menuItemsRepository.findMenuItemsWithOrders()).thenReturn(Collections.emptyList());

        List<MenuItemsDTO.Response> result = menuItemsService.getMenuItemsWithOrders();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemByOrderItemId()
    // =========================================================================

    @Test
    void getMenuItemByOrderItemId_positive() {
        when(menuItemsRepository.findByOrderItemId(50)).thenReturn(Optional.of(menuItem));

        MenuItemsDTO.Response result = menuItemsService.getMenuItemByOrderItemId(50);

        assertEquals("Margherita Pizza", result.getItemName());
    }

    @Test
    void getMenuItemByOrderItemId_negative() {
        when(menuItemsRepository.findByOrderItemId(999)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> menuItemsService.getMenuItemByOrderItemId(999));
    }

    // =========================================================================
    // getMenuItemsWithRestaurantByRestaurantId()
    // =========================================================================

    @Test
    void getMenuItemsWithRestaurantByRestaurantId_positive() {
        List<Object[]> rows =  java.util.Collections.singletonList(
                new Object[]{10, "Margherita Pizza", "Classic", 299.0, 1, "Pizza Palace"});
        when(menuItemsRepository.findMenuItemsWithRestaurantByRestaurantId(1)).thenReturn(rows);

        List<Object[]> result =
                menuItemsService.getMenuItemsWithRestaurantByRestaurantId(1);

        assertEquals(1, result.size());
        assertEquals("Margherita Pizza", result.get(0)[1]);
    }

    @Test
    void getMenuItemsWithRestaurantByRestaurantId_negative() {
        when(menuItemsRepository.findMenuItemsWithRestaurantByRestaurantId(99))
                .thenReturn(Collections.emptyList());

        List<Object[]> result =
                menuItemsService.getMenuItemsWithRestaurantByRestaurantId(99);

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getAveragePricePerRestaurant()
    // =========================================================================

    @Test
    void getAveragePricePerRestaurant_positive() {
        List<Object[]> rows =  java.util.Collections.singletonList(new Object[]{1, "Pizza Palace", 250.0});
        when(menuItemsRepository.findAveragePricePerRestaurant()).thenReturn(rows);

        List<Object[]> result = menuItemsService.getAveragePricePerRestaurant();

        assertEquals(1, result.size());
        assertEquals(250.0, result.get(0)[2]);
    }

    @Test
    void getAveragePricePerRestaurant_negative() {
        when(menuItemsRepository.findAveragePricePerRestaurant())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = menuItemsService.getAveragePricePerRestaurant();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMenuItemsWithOrderCount()
    // =========================================================================

    @Test
    void getMenuItemsWithOrderCount_positive() {
        List<Object[]> rows =  java.util.Collections.singletonList(new Object[]{10, "Margherita Pizza", 299.0, 8L});
        when(menuItemsRepository.findMenuItemsWithOrderCount()).thenReturn(rows);

        List<Object[]> result = menuItemsService.getMenuItemsWithOrderCount();

        assertEquals(1, result.size());
        assertEquals(8L, result.get(0)[3]);
    }

    @Test
    void getMenuItemsWithOrderCount_negative() {
        when(menuItemsRepository.findMenuItemsWithOrderCount())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = menuItemsService.getMenuItemsWithOrderCount();

        assertTrue(result.isEmpty());
    }

    // =========================================================================
    // getMostOrderedMenuItems()
    // =========================================================================

    @Test
    void getMostOrderedMenuItems_positive() {
        List<Object[]> rows =  java.util.Collections.singletonList(
                new Object[]{10, "Margherita Pizza", "Pizza Palace", 20L});
        when(menuItemsRepository.findMostOrderedMenuItems()).thenReturn(rows);

        List<Object[]> result = menuItemsService.getMostOrderedMenuItems();

        assertEquals(1, result.size());
        assertEquals(20L, result.get(0)[3]);
    }

    @Test
    void getMostOrderedMenuItems_negative() {
        when(menuItemsRepository.findMostOrderedMenuItems())
                .thenReturn(Collections.emptyList());

        List<Object[]> result = menuItemsService.getMostOrderedMenuItems();

        assertTrue(result.isEmpty());
    }
}