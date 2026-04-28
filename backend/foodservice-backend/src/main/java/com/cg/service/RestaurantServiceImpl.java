package com.cg.service;

import com.cg.dto.RestaurantRequestDTO;
import com.cg.dto.RestaurantResponseDTO;
import com.cg.entity.Restaurant;
import com.cg.repo.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // =========================================================================
    // HELPER — Entity → ResponseDTO
    // Converts Restaurant entity to RestaurantResponseDTO.
    // Relationship lists are mapped to ID lists to prevent infinite recursion.
    // =========================================================================

    private RestaurantResponseDTO toResponseDTO(Restaurant restaurant) {
        List<Integer> menuItemIds = restaurant.getMenuItems()
                .stream()
                .map(m -> m.getItemId())
                .collect(Collectors.toList());

        List<Integer> orderIds = restaurant.getOrders()
                .stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        List<Integer> ratingIds = restaurant.getRatings()
                .stream()
                .map(r -> r.getRatingId())
                .collect(Collectors.toList());

        return new RestaurantResponseDTO(
                restaurant.getRestaurantId(),
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress(),
                restaurant.getRestaurantPhone(),
                menuItemIds,
                orderIds,
                ratingIds
        );
    }

    // =========================================================================
    // HELPER — RequestDTO → Entity
    // Maps only scalar fields. Relationships are managed separately.
    // =========================================================================

    private Restaurant toEntity(RestaurantRequestDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(dto.getRestaurantId());
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setRestaurantAddress(dto.getRestaurantAddress());
        restaurant.setRestaurantPhone(dto.getRestaurantPhone());
        return restaurant;
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public RestaurantResponseDTO addRestaurant(RestaurantRequestDTO requestDTO) {
        // restaurantId is manually supplied (no @GeneratedValue on entity)
        if (restaurantRepository.existsById(requestDTO.getRestaurantId())) {
            throw new RuntimeException("Restaurant with ID " + requestDTO.getRestaurantId() + " already exists.");
        }
        Restaurant restaurant = toEntity(requestDTO);
        Restaurant saved = restaurantRepository.save(restaurant);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantById(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        return toResponseDTO(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantResponseDTO updateRestaurant(Integer restaurantId, RestaurantRequestDTO requestDTO) {
        Restaurant existing = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        // Update only scalar fields; relationships are untouched
        existing.setRestaurantName(requestDTO.getRestaurantName());
        existing.setRestaurantAddress(requestDTO.getRestaurantAddress());
        existing.setRestaurantPhone(requestDTO.getRestaurantPhone());

        Restaurant updated = restaurantRepository.save(existing);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteRestaurant(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
        }
        restaurantRepository.deleteById(restaurantId);
    }

    // =========================================================================
    // DERIVED QUERY METHODS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantByName(String restaurantName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with name: " + restaurantName));
        return toResponseDTO(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> searchRestaurantsByName(String keyword) {
        return restaurantRepository.findByRestaurantNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantByPhone(String restaurantPhone) {
        Restaurant restaurant = restaurantRepository.findByRestaurantPhone(restaurantPhone)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with phone: " + restaurantPhone));
        return toResponseDTO(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> searchRestaurantsByAddress(String address) {
        return restaurantRepository.findByRestaurantAddressContainingIgnoreCase(address)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // JPQL / RELATIONSHIP-BASED METHODS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsHavingMenuItems() {
        return restaurantRepository.findRestaurantsHavingMenuItems()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantByMenuItemId(int itemId) {
        Restaurant restaurant = restaurantRepository.findByMenuItemId(itemId)
                .orElseThrow(() -> new RuntimeException("No restaurant found for menu item ID: " + itemId));
        return toResponseDTO(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsByMenuItemName(String keyword) {
        return restaurantRepository.findByMenuItemNameContaining(keyword)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsByMenuItemPriceRange(Double minPrice, Double maxPrice) {
        return restaurantRepository.findByMenuItemPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsHavingOrders() {
        return restaurantRepository.findRestaurantsHavingOrders()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurantByOrderId(Integer orderId) {
        Restaurant restaurant = restaurantRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No restaurant found for order ID: " + orderId));
        return toResponseDTO(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsHavingRatings() {
        return restaurantRepository.findRestaurantsHavingRatings()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsByMinAverageRating(Double minRating) {
        return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // NATIVE QUERY METHODS — raw Object[] returned as-is to Controller
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAllWithMenuItemCount() {
        return restaurantRepository.findAllWithMenuItemCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAllWithOrderCount() {
        return restaurantRepository.findAllWithOrderCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAllWithAverageRating() {
        return restaurantRepository.findAllWithAverageRating();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAllWithFullSummary() {
        return restaurantRepository.findAllWithFullSummary();
    }
}