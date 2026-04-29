package com.cg.dto;

import java.time.LocalDateTime;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;


public class OrderDTO {

	private Integer orderId;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    private Integer deliveryDriverId; 

    @NotNull(message = "Order status is required")
    @Size(min = 3, message = "Order status must be valid")
    private String orderStatus;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    private Set<Integer> couponIds; 
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

}