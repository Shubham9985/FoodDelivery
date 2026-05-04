package com.cg.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RestaurantRequestDTO {

    // ─── restaurantId ──────────────────────────────────────────────────────────
    // Manually supplied (no @GeneratedValue on entity) so it must be present and positive.
    // @NotNull  → rejects missing/null ID in the JSON body.
    // @Positive → rejects 0 and negative values (valid IDs always start from 1).



    // ─── restaurantName ────────────────────────────────────────────────────────
    // @NotBlank → rejects null, "", and "   " (covers @NotNull + @NotEmpty + trim check).
    // @Size     → mirrors the @Column(length = 255) constraint on the entity so the
    //             error is caught in the DTO before it ever reaches JPA.

    @NotBlank(message = "Restaurant name must not be blank")
    @Size(max = 255, message = "Restaurant name must not exceed 255 characters")
    private String restaurantName;

    // ─── restaurantAddress ─────────────────────────────────────────────────────
    // Same reasoning as restaurantName — mirrors @Column(length = 255).

    @NotBlank(message = "Restaurant address must not be blank")
    @Size(max = 255, message = "Restaurant address must not exceed 255 characters")
    private String restaurantAddress;

    // ─── restaurantPhone ───────────────────────────────────────────────────────
    // @NotBlank → rejects null and blank values.
    // @Size     → mirrors @Column(length = 20) on the entity.
    // @Pattern  → allows digits, spaces, hyphens, and a leading '+' for international format.
    //             Examples accepted:  9876543210  |  +91-98765-43210  |  011 2345 6789
    //             Examples rejected:  abc123  |  !!@@  |  (empty string)

    @NotBlank(message = "Restaurant phone must not be blank")
    @Size(max = 20, message = "Restaurant phone must not exceed 20 characters")
    @Pattern(
        regexp = "^[+]?[0-9][0-9\\-\\s]{6,18}[0-9]$",
        message = "Restaurant phone must be a valid phone number (7–20 digits, optional +, spaces, hyphens)"
    )
    private String restaurantPhone;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public RestaurantRequestDTO() {}

    public RestaurantRequestDTO(
                                String restaurantName,
                                String restaurantAddress,
                                String restaurantPhone) {
       
        this.restaurantName    = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantPhone   = restaurantPhone;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

   

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }

    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }

    @Override
    public String toString() {
        return "RestaurantRequestDTO{" +
               ", restaurantName='" + restaurantName + '\'' +
               ", restaurantAddress='" + restaurantAddress + '\'' +
               ", restaurantPhone='" + restaurantPhone + '\'' +
               '}';
    }
}