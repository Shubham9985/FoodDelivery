package com.cg.dto;


import java.util.Set;

public class CustomerDTO {

    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private Set<DeliveryAddressDTO> addresses;

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

    public Set<DeliveryAddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<DeliveryAddressDTO> addresses) {
        this.addresses = addresses;
    }
}