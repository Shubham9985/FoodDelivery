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
	
	@OneToMany(mappedBy = "deliveryDriver")
    private Set<Order> assignedOrders;

	public Integer getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(Integer driver_id) {
		this.driver_id = driver_id;
	}

	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public String getDriver_phone() {
		return driver_phone;
	}

	public void setDriver_phone(String driver_phone) {
		this.driver_phone = driver_phone;
	}

	public String getDriver_vehicle() {
		return driver_vehicle;
	}

	public void setDriver_vehicle(String driver_vehicle) {
		this.driver_vehicle = driver_vehicle;
	}

	public Set<Order> getAssignedOrders() {
		return assignedOrders;
	}

	public void setAssignedOrders(Set<Order> assignedOrders) {
		this.assignedOrders = assignedOrders;
	}
	
	
}
