package com.cg.dto;

import java.util.List;

public class MenuItemsDTO {

    // =========================================================================
    // REQUEST DTO — used for POST (create) and PUT (update)
    // =========================================================================
    public static class Request {

        // itemId excluded — @GeneratedValue(IDENTITY) on entity, DB assigns it

        private String itemName;
        private String itemDescription;
        private Double itemPrice;

        // From @ManyToOne Restaurant
        // Client sends only the FK — not the full Restaurant object
        private Integer restaurantId;

        // From @OneToMany List<OrderItem>
        // Excluded — client never sends order data when creating a menu item

        // ─── Constructors ─────────────────────────────────────────────────────

        public Request() {}

        public Request(String itemName,
                       String itemDescription,
                       Double itemPrice,
                       Integer restaurantId) {
            this.itemName        = itemName;
            this.itemDescription = itemDescription;
            this.itemPrice       = itemPrice;
            this.restaurantId    = restaurantId;
        }

        // ─── Getters & Setters ────────────────────────────────────────────────

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

        public Double getItemPrice() { return itemPrice; }
        public void setItemPrice(Double itemPrice) { this.itemPrice = itemPrice; }

        public Integer getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

        @Override
        public String toString() {
            return "MenuItemsDTO.Request{" +
                   "itemName='" + itemName + '\'' +
                   ", itemDescription='" + itemDescription + '\'' +
                   ", itemPrice=" + itemPrice +
                   ", restaurantId=" + restaurantId +
                   '}';
        }
    }

    // =========================================================================
    // RESPONSE DTO — used for GET, POST and PUT responses sent back to client
    // =========================================================================
    public static class Response {

        // All entity fields included in response
        private int    itemId;
        private String itemName;
        private String itemDescription;
        private Double itemPrice;

        // From @ManyToOne Restaurant
        // Flattened to avoid Restaurant → MenuItems → Restaurant infinite recursion
        private Integer restaurantId;
        private String  restaurantName;

        // From @OneToMany List<OrderItem>
        // Exposed as ID list only — avoids LazyInitializationException
        private List<Integer> orderItemIds;

        // ─── Constructors ─────────────────────────────────────────────────────

        public Response() {}

        public Response(int itemId,
                        String itemName,
                        String itemDescription,
                        Double itemPrice,
                        Integer restaurantId,
                        String restaurantName,
                        List<Integer> orderItemIds) {
            this.itemId          = itemId;
            this.itemName        = itemName;
            this.itemDescription = itemDescription;
            this.itemPrice       = itemPrice;
            this.restaurantId    = restaurantId;
            this.restaurantName  = restaurantName;
            this.orderItemIds    = orderItemIds;
        }

        // ─── Getters & Setters ────────────────────────────────────────────────

        public int getItemId() { return itemId; }
        public void setItemId(int itemId) { this.itemId = itemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

        public Double getItemPrice() { return itemPrice; }
        public void setItemPrice(Double itemPrice) { this.itemPrice = itemPrice; }

        public Integer getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

        public List<Integer> getOrderItemIds() { return orderItemIds; }
        public void setOrderItemIds(List<Integer> orderItemIds) { this.orderItemIds = orderItemIds; }

        @Override
        public String toString() {
            return "MenuItemsDTO.Response{" +
                   "itemId=" + itemId +
                   ", itemName='" + itemName + '\'' +
                   ", itemDescription='" + itemDescription + '\'' +
                   ", itemPrice=" + itemPrice +
                   ", restaurantId=" + restaurantId +
                   ", restaurantName='" + restaurantName + '\'' +
                   ", orderItemIds=" + orderItemIds +
                   '}';
        }
    }
}