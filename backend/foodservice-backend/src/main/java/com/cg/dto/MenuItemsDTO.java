package com.cg.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MenuItemsDTO {

    // =========================================================================
    // REQUEST DTO — used for POST (create) and PUT (update)
    // =========================================================================
    public static class Request {

        // ─── itemId ────────────────────────────────────────────────────────────
        // Excluded intentionally — @GeneratedValue(IDENTITY) on entity.
        // The DB assigns it; the client must never send it.

        // ─── itemName ──────────────────────────────────────────────────────────
        // @NotBlank → rejects null, "", and whitespace-only strings.
        // @Size     → no explicit @Column length on entity field, applying a
        //             safe 255-char ceiling consistent with the DB VARCHAR default.

        @NotBlank(message = "Item name must not be blank")
        @Size(max = 255, message = "Item name must not exceed 255 characters")
        private String itemName;

        // ─── itemDescription ───────────────────────────────────────────────────
        // @NotBlank → a description is required; blank descriptions are meaningless.
        // @Size     → 500-char ceiling gives room for a proper description without
        //             letting clients send unbounded text.

        @NotBlank(message = "Item description must not be blank")
        @Size(max = 500, message = "Item description must not exceed 500 characters")
        private String itemDescription;

        // ─── itemPrice ─────────────────────────────────────────────────────────
        // @NotNull      → price must always be present; no free items by accident.
        // @DecimalMin   → price must be greater than 0.00 (inclusive=false).
        //                 "0.00" itself is also rejected — a zero-priced item
        //                 is almost certainly a data entry mistake.
        // @Digits       → caps integer part at 8 digits and fraction at 2 digits,
        //                 matching a standard DECIMAL(10,2) column.
        //                 Rejects values like 999999999.999 before they reach JPA.

        @NotNull(message = "Item price must not be null")
        @DecimalMin(value = "0.01", message = "Item price must be greater than 0")
        @Digits(
            integer = 8,
            fraction = 2,
            message = "Item price must have at most 8 integer digits and 2 decimal places"
        )
        private Double itemPrice;

        // ─── restaurantId ──────────────────────────────────────────────────────
        // @NotNull  → every menu item must belong to a restaurant; orphan items
        //             are not allowed (matches @ManyToOne on entity + FK column).
        // @Positive → valid restaurant IDs always start from 1 (manually assigned,
        //             no @GeneratedValue — so 0 and negatives are always wrong).
        // Note: existence of this restaurantId in the DB is checked in the service
        //       layer (requires a DB lookup — Bean Validation can't do that).

        @NotNull(message = "Restaurant ID must not be null")
        @Positive(message = "Restaurant ID must be a positive integer")
        private Integer restaurantId;

        // ─── orderItemIds excluded ─────────────────────────────────────────────
        // Client never sends order data when creating or updating a menu item.
        // Orders are managed through the Order/OrderItem flow, not here.

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
    }

    // =========================================================================
    // RESPONSE DTO — used for GET, POST and PUT responses sent back to client.
    // No validation annotations here — responses are never validated as input.
    // =========================================================================
    public static class Response {

        private int    itemId;
        private String itemName;
        private String itemDescription;
        private Double itemPrice;

        // Flattened from @ManyToOne Restaurant — avoids infinite recursion
        private Integer restaurantId;
        private String  restaurantName;

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