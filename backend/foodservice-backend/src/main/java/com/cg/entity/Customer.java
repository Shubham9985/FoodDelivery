package com.cg.entity;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
    
}