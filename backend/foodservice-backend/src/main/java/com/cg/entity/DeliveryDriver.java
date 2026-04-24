package com.cg.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class DeliveryDriver {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer driver_id;
	private String driver_name;
	private String driver_phone;
	private String driver_vehicle;
	
	@OneToMany(mappedBy = "driver")
    private Set<Order> assignedOrders;
}
