package com.cg.service;

import com.cg.dto.MenuItemsDTO;
import com.cg.entity.MenuItems;
import com.cg.entity.Restaurant;
import com.cg.exceptions.IdNotFoundException;
import com.cg.exceptions.NameNotFoundException;
import com.cg.repo.MenuItemsRepository;
import com.cg.repo.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemsServiceImpl implements MenuItemsService {

    private final MenuItemsRepository menuItemsRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenuItemsServiceImpl(MenuItemsRepository menuItemsRepository,
                                RestaurantRepository restaurantRepository) {
        this.menuItemsRepository = menuItemsRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // =========================================================================
    // HELPER — Entity → Response DTO
    // Flattens @ManyToOne Restaurant to restaurantId + restaurantName.
    // Exposes @OneToMany List<OrderItem> as a list of orderItemIds.
    // Avoids LazyInitializationException and infinite recursion.
    // =========================================================================

    private MenuItemsDTO.Response toResponseDTO(MenuItems item) {
        Integer restaurantId   = null;
        String  restaurantName = null;
        if (item.getRestaurant() != null) {
            restaurantId   = item.getRestaurant().getRestaurantId();
            restaurantName = item.getRestaurant().getRestaurantName();
        }

        List<Integer> orderItemIds = null;
        if (item.getOrderItems() != null) {
            orderItemIds = item.getOrderItems()
                    .stream()
                    .map(oi -> oi.getOrderItemId())
                    .collect(Collectors.toList());
        }

        return new MenuItemsDTO.Response(
                item.getItemId(),
                item.getItemName(),
                item.getItemDescription(),
                item.getItemPrice(),
                restaurantId,
                restaurantName,
                orderItemIds
        );
    }

    // =========================================================================
    // HELPER — Request DTO → Entity
    // Resolves restaurantId FK to a managed Restaurant entity.
    // @OneToMany orderItems not set here — they are owned by OrderItem side.
    // =========================================================================

    private MenuItems toEntity(MenuItemsDTO.Request dto) {
        MenuItems item = new MenuItems();
        item.setItemName(dto.getItemName());
        item.setItemDescription(dto.getItemDescription());
        item.setItemPrice(dto.getItemPrice());

        if (dto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new IdNotFoundException(
                            "Restaurant not found with ID: " + dto.getRestaurantId()));
            item.setRestaurant(restaurant);
        }

        return item;
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public MenuItemsDTO.Response addMenuItem(MenuItemsDTO.Request requestDTO) {
        // itemId is @GeneratedValue(IDENTITY) — DB assigns it; not supplied by client
        MenuItems item = toEntity(requestDTO);
        MenuItems saved = menuItemsRepository.save(item);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemsDTO.Response getMenuItemById(Integer itemId) {
        MenuItems item = menuItemsRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Menu item not found with ID: " + itemId));
        return toResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getAllMenuItems() {
        return menuItemsRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemsDTO.Response updateMenuItem(Integer itemId, MenuItemsDTO.Request requestDTO) {
        MenuItems existing = menuItemsRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Menu item not found with ID: " + itemId));

        existing.setItemName(requestDTO.getItemName());
        existing.setItemDescription(requestDTO.getItemDescription());
        existing.setItemPrice(requestDTO.getItemPrice());

        // Re-assign restaurant if restaurantId changed
        if (requestDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(requestDTO.getRestaurantId())
                    .orElseThrow(() -> new IdNotFoundException(
                            "Restaurant not found with ID: " + requestDTO.getRestaurantId()));
            existing.setRestaurant(restaurant);
        }

        MenuItems updated = menuItemsRepository.save(existing);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteMenuItem(Integer itemId) {
        if (!menuItemsRepository.existsById(itemId)) {
            throw new IdNotFoundException("Menu item not found with ID: " + itemId);
        }
        menuItemsRepository.deleteById(itemId);
    }

    // =========================================================================
    // DERIVED QUERY METHODS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public MenuItemsDTO.Response getMenuItemByName(String itemName) {
        MenuItems item = menuItemsRepository.findByItemName(itemName)
                .orElseThrow(() -> new NameNotFoundException("Menu item not found with name: " + itemName));
        return toResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> searchMenuItemsByName(String keyword) {
        return menuItemsRepository.findByItemNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByMaxPrice(Double price) {
        return menuItemsRepository.findByItemPriceLessThanEqual(price)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByMinPrice(Double price) {
        return menuItemsRepository.findByItemPriceGreaterThanEqual(price)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByPriceRange(Double minPrice, Double maxPrice) {
        return menuItemsRepository.findByItemPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // DERIVED QUERY METHODS — via Restaurant FK
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByRestaurantId(Integer restaurantId) {
        return menuItemsRepository.findByRestaurant_RestaurantId(restaurantId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByRestaurantName(String restaurantName) {
        return menuItemsRepository.findByRestaurant_RestaurantName(restaurantName)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> searchMenuItemsByNameInRestaurant(Integer restaurantId, String keyword) {
        return menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemNameContainingIgnoreCase(restaurantId, keyword)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByPriceRangeInRestaurant(
            Integer restaurantId, Double minPrice, Double maxPrice) {
        return menuItemsRepository
                .findByRestaurant_RestaurantIdAndItemPriceBetween(restaurantId, minPrice, maxPrice)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkMenuItemExistsInRestaurant(String itemName, Integer restaurantId) {
        return menuItemsRepository.existsByItemNameAndRestaurant_RestaurantId(itemName, restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countMenuItemsByRestaurant(Integer restaurantId) {
        return menuItemsRepository.countByRestaurant_RestaurantId(restaurantId);
    }

    // =========================================================================
    // JPQL METHODS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByRestaurantIdOrderByPriceAsc(Integer restaurantId) {
        return menuItemsRepository.findByRestaurantIdOrderByPriceAsc(restaurantId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsByRestaurantIdOrderByPriceDesc(Integer restaurantId) {
        return menuItemsRepository.findByRestaurantIdOrderByPriceDesc(restaurantId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemsDTO.Response getMostExpensiveMenuItemByRestaurant(Integer restaurantId) {
        MenuItems item = menuItemsRepository.findMostExpensiveByRestaurantId(restaurantId)
                .orElseThrow(() -> new IdNotFoundException(
                        "No menu items found for restaurant ID: " + restaurantId));
        return toResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemsDTO.Response getCheapestMenuItemByRestaurant(Integer restaurantId) {
        MenuItems item = menuItemsRepository.findCheapestByRestaurantId(restaurantId)
                .orElseThrow(() -> new IdNotFoundException(
                        "No menu items found for restaurant ID: " + restaurantId));
        return toResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemsDTO.Response> getMenuItemsWithOrders() {
        return menuItemsRepository.findMenuItemsWithOrders()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemsDTO.Response getMenuItemByOrderItemId(Integer orderItemId) {
        MenuItems item = menuItemsRepository.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new IdNotFoundException(
                        "No menu item found for order item ID: " + orderItemId));
        return toResponseDTO(item);
    }

    // =========================================================================
    // NATIVE QUERY METHODS — raw Object[] returned as-is to Controller
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMenuItemsWithRestaurantByRestaurantId(Integer restaurantId) {
        return menuItemsRepository.findMenuItemsWithRestaurantByRestaurantId(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAveragePricePerRestaurant() {
        return menuItemsRepository.findAveragePricePerRestaurant();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMenuItemsWithOrderCount() {
        return menuItemsRepository.findMenuItemsWithOrderCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostOrderedMenuItems() {
        return menuItemsRepository.findMostOrderedMenuItems();
    }
}