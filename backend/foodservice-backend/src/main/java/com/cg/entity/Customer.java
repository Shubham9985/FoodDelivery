package com.cg.entity;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Customer {

    @Id
    private Integer customerId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private Set<Order> orders;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private Set<DeliveryAddress> addresses;

    
    public Integer getCustomerId() { 
    	return customerId; 
    	}
    public void setCustomerId(Integer customerId) {
    	this.customerId = customerId; 
    	}

    public String getCustomerName() { 
    	return customerName; 
    	}
    public void setCustomerName(String customerName) {
    	this.customerName = customerName; 
    	}

    public String getCustomerEmail() {
    	return customerEmail; 
    	}
    public void setCustomerEmail(String customerEmail) {
    	this.customerEmail = customerEmail; 
    	}

    public String getCustomerPhone() {
    	return customerPhone; 
    	}
    public void setCustomerPhone(String customerPhone) {
    	this.customerPhone = customerPhone; 
    	}

    public Set<Order> getOrders() {
    	return orders; 
    	}
    public void setOrders(Set<Order> orders) {
    	this.orders = orders; 
    	}

    public Set<DeliveryAddress> getAddresses() {
    	return addresses; 
    	}
    public void setAddresses(Set<DeliveryAddress> addresses) {
    	this.addresses = addresses; 
    	}
}