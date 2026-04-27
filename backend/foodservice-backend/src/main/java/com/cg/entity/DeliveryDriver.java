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
	private Integer driverId;
	
	private String driverName;
	
	private String driverPhone;
	
	private String driverVehicle;
	
	@OneToMany(mappedBy = "deliveryDriver")
    private Set<Order> assignedOrders;


	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getDriverVehicle() {
		return driverVehicle;
	}

	public void setDriverVehicle(String driverVehicle) {
		this.driverVehicle = driverVehicle;
	}

	public Set<Order> getAssignedOrders() {
		return assignedOrders;
	}

	public void setAssignedOrders(Set<Order> assignedOrders) {
		this.assignedOrders = assignedOrders;
	}
	
	
}
