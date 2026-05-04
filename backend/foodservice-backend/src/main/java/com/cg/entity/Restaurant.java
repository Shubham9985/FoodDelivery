package com.cg.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "restaurant_name", length = 255)
    private String restaurantName;

    @Column(name = "restaurant_address", length = 255)
    private String restaurantAddress;

    @Column(name = "restaurant_phone", length = 20)
    private String restaurantPhone;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MenuItems> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();

    public Restaurant() {}

    public Restaurant(String restaurantName, String restaurantAddress, String restaurantPhone) {
        this.restaurantName    = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone   = restaurantPhone;
    }

    public void addMenuItem(MenuItems item) {
        item.setRestaurant(this);
        this.menuItems.add(item);
    }

    public void removeMenuItem(MenuItems item) {
        item.setRestaurant(null);
        this.menuItems.remove(item);
    }

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

    public List<MenuItems> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItems> menuItems) { this.menuItems = menuItems; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }
}