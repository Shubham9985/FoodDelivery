package com.cg.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonPropertyOrder({ "customerId", "customerName", "customerEmail", "customerPhone", "userId", "addresses" })
public class CustomerDTO {

    private Integer customerId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 50)
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String customerPhone;

    @NotNull(message = "User ID is required")
    private Integer userId;

    private Set<DeliveryAddressDTO> addresses;

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Set<DeliveryAddressDTO> getAddresses() { return addresses; }
    public void setAddresses(Set<DeliveryAddressDTO> addresses) { this.addresses = addresses; }
}