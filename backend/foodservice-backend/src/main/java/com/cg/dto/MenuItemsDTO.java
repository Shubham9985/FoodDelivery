package com.cg.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MenuItemsDTO {

    public static class Request {

        @NotBlank(message = "Item name must not be blank")
        @Size(max = 255)
        private String itemName;

        @NotBlank(message = "Item description must not be blank")
        @Size(max = 500)
        private String itemDescription;

        @NotNull(message = "Item price must not be null")
        @DecimalMin(value = "0.01", message = "Item price must be greater than 0")
        @Digits(integer = 8, fraction = 2)
        private BigDecimal itemPrice;

        @NotNull(message = "Restaurant ID must not be null")
        @Positive
        private Integer restaurantId;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        private String itemImageUrl;

        public Request() {}

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

        public BigDecimal getItemPrice() { return itemPrice; }
        public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

        public Integer getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

        public String getItemImageUrl() { return itemImageUrl; }
        public void setItemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; }
    }

    public static class Response {

        private Integer itemId;
        private String itemName;
        private String itemDescription;
        private BigDecimal itemPrice;
        private Integer restaurantId;
        private String restaurantName;
        private String itemImageUrl;
        private List<Integer> orderItemIds;

        public Response() {}

        public Integer getItemId() { return itemId; }
        public void setItemId(Integer itemId) { this.itemId = itemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

        public BigDecimal getItemPrice() { return itemPrice; }
        public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

        public Integer getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

        public String getItemImageUrl() { return itemImageUrl; }
        public void setItemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; }

        public List<Integer> getOrderItemIds() { return orderItemIds; }
        public void setOrderItemIds(List<Integer> orderItemIds) { this.orderItemIds = orderItemIds; }
    }
}