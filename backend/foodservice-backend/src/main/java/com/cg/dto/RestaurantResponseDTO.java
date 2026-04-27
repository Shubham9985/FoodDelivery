package com.cg.dto;

import java.util.List;

public class RestaurantResponseDTO {

    // ─── Direct fields from Restaurant entity ─────────────────────────────────

    private Integer restaurantId;       // included in response, not in request
    private String  restaurantName;
    private String  restaurantAddress;
    private String  restaurantPhone;

    // ─── Relationships exposed as ID lists ────────────────────────────────────
    // Matches your entity's: List<MenuItems>, List<Order>, List<Rating>
    // Sent as IDs only — prevents Jackson infinite recursion on bidirectional refs

    private List<Integer> menuItemIds;  // from List<MenuItems>
    private List<Integer> orderIds;     // from List<Order>
    private List<Integer> ratingIds;    // from List<Rating>

    // ─── Constructors ─────────────────────────────────────────────────────────

    public RestaurantResponseDTO() {}

    public RestaurantResponseDTO(Integer restaurantId,
                                 String restaurantName,
                                 String restaurantAddress,
                                 String restaurantPhone,
                                 List<Integer> menuItemIds,
                                 List<Integer> orderIds,
                                 List<Integer> ratingIds) {
        this.restaurantId      = restaurantId;
        this.restaurantName    = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone   = restaurantPhone;
        this.menuItemIds       = menuItemIds;
        this.orderIds          = orderIds;
        this.ratingIds         = ratingIds;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

    public List<Integer> getMenuItemIds() { return menuItemIds; }
    public void setMenuItemIds(List<Integer> menuItemIds) { this.menuItemIds = menuItemIds; }

    public List<Integer> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Integer> orderIds) { this.orderIds = orderIds; }

    public List<Integer> getRatingIds() { return ratingIds; }
    public void setRatingIds(List<Integer> ratingIds) { this.ratingIds = ratingIds; }

    @Override
    public String toString() {
        return "RestaurantResponseDTO{" +
               "restaurantId=" + restaurantId +
               ", restaurantName='" + restaurantName + '\'' +
               ", restaurantAddress='" + restaurantAddress + '\'' +
               ", restaurantPhone='" + restaurantPhone + '\'' +
               ", menuItemIds=" + menuItemIds +
               ", orderIds=" + orderIds +
               ", ratingIds=" + ratingIds +
               '}';
    }
}