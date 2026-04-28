package com.cg.dto;

import java.util.List;

public class RestaurantResponseDTO {


    private Integer restaurantId;       
    private String  restaurantName;
    private String  restaurantAddress;
    private String  restaurantPhone;


    private List<Integer> menuItemIds;  
    private List<Integer> orderIds;     
    private List<Integer> ratingIds;    


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