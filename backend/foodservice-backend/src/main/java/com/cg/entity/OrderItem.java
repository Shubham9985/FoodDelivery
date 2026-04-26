package com.cg.entity;

import jakarta.persistence.*;

@Entity
@Table
public class OrderItem {
    @Id
    private int orderItemId;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem menuItem;

}