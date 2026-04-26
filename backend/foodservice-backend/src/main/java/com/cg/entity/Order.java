package com.cg.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Order {

    @Id
    private int orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    @ManyToOne
    @JoinColumn(name = "delivery_driver_id")
    private DeliveryDriver deliveryDriver;

   
}