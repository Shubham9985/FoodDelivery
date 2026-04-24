package com.cg.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Restaurants")
public class Restaurant {

    // ─── Primary Key ──────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer restaurantId;

    // ─── Basic Fields ─────────────────────────────────────────────────────────

    @Column(name = "restaurant_name", length = 255)
    private String restaurantName;

    @Column(name = "restaurant_address", length = 255)
    private String restaurantAddress;

    @Column(name = "restaurant_phone", length = 20)
    private String restaurantPhone;

    // ─── Relationships ────────────────────────────────────────────────────────

    /**
     * One Restaurant → Many MenuItems
     * Mapped by the restaurant_id FK in the MenuItems table.
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MenuItem> menuItems = new ArrayList<>();

    /**
     * One Restaurant → Many Orders
     * Mapped by the restaurant_id FK in the Orders table.
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    /**
     * One Restaurant → Many Ratings
     * Mapped by the restaurant_id FK in the Ratings table.
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Restaurant() {}

    public Restaurant(String restaurantName, String restaurantAddress, String restaurantPhone) {
        this.restaurantName    = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone   = restaurantPhone;
    }

    // ─── Convenience helpers ──────────────────────────────────────────────────

    /** Keeps the bi-directional link consistent when adding a menu item. */
    public void addMenuItem(MenuItem item) {
        item.setRestaurant(this);
        this.menuItems.add(item);
    }

    /** Keeps the bi-directional link consistent when removing a menu item. */
    public void removeMenuItem(MenuItem item) {
        item.setRestaurant(null);
        this.menuItems.remove(item);
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    // ─── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Restaurant{" +
               "restaurantId=" + restaurantId +
               ", restaurantName='" + restaurantName + '\'' +
               ", restaurantAddress='" + restaurantAddress + '\'' +
               ", restaurantPhone='" + restaurantPhone + '\'' +
               '}';
    }
}
