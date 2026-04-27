package com.cg.dto;

public class RestaurantRequestDTO {

    // restaurantId included — entity has no @GeneratedValue so ID is manually supplied
    private Integer restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhone;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public RestaurantRequestDTO() {}

    public RestaurantRequestDTO(Integer restaurantId,
                                String restaurantName,
                                String restaurantAddress,
                                String restaurantPhone) {
        this.restaurantId      = restaurantId;
        this.restaurantName    = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone   = restaurantPhone;
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

    @Override
    public String toString() {
        return "RestaurantRequestDTO{" +
               "restaurantId=" + restaurantId +
               ", restaurantName='" + restaurantName + '\'' +
               ", restaurantAddress='" + restaurantAddress + '\'' +
               ", restaurantPhone='" + restaurantPhone + '\'' +
               '}';
    }
}