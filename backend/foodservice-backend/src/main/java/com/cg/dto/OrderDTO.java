package com.cg.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class OrderDTO {

	private Integer orderId;
    private Integer customerId;
    private Integer restaurantId;
    private Integer deliveryDriverId;

    private String orderStatus;
    private LocalDateTime orderDate;

    private Set<Integer> couponIds;

    private Set<OrderItemDTO> items;
    
    public Integer getOrderId() {
    	return orderId;
    }
    
    public void setOrderId(Integer orderId) {
    	this.orderId=orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getDeliveryDriverId() {
        return deliveryDriverId;
    }

    public void setDeliveryDriverId(Integer deliveryDriverId) {
        this.deliveryDriverId = deliveryDriverId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Set<Integer> getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(Set<Integer> couponIds) {
        this.couponIds = couponIds;
    }

    public Set<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(Set<OrderItemDTO> items) {
        this.items = items;
    }
}