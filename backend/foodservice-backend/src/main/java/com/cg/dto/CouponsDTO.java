package com.cg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CouponsDTO {

	private Integer couponId;

    @NotBlank(message = "Coupon code cannot be empty")
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Coupon must be uppercase alphanumeric")
    private String couponCode;

    @NotNull
    @DecimalMin(value = "0.01", message = "Discount must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    @Positive
    private BigDecimal discountAmount;

    @NotNull
    @Future
    private LocalDate expiryDate;

    public CouponsDTO() {}

    public CouponsDTO(Integer couponId, String couponCode, BigDecimal discountAmount, LocalDate expiryDate) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
