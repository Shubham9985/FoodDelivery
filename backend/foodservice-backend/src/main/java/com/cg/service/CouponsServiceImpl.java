package com.cg.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.entity.Coupons;
import com.cg.repo.CouponsRepository;

@Service
public class CouponsServiceImpl implements CouponsService {

    @Autowired
    private CouponsRepository couponsRepository;

    @Override
    public Coupons addCoupon(Coupons coupon) {

        // Duplicate check
        if (couponsRepository.existsByCouponCode(coupon.getCouponCode())) {
            throw new RuntimeException("Coupon code already exists");
        }

        // Expiry validation
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot add expired coupon");
        }

        return couponsRepository.save(coupon);
    }

    @Override
    public Coupons getCouponById(Integer couponId) {
        return couponsRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public Coupons getCouponByCode(String couponCode) {
        return couponsRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public List<Coupons> getAllCoupons() {
        return couponsRepository.findAll();
    }

    @Override
    public Coupons updateCoupon(Integer couponId, Coupons coupon) {

        Coupons existing = getCouponById(couponId);

        // Prevent duplicate on update
        if (!existing.getCouponCode().equals(coupon.getCouponCode()) &&
            couponsRepository.existsByCouponCode(coupon.getCouponCode())) {
            throw new RuntimeException("Coupon code already exists");
        }

        // Expiry validation
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot set expired coupon");
        }

        existing.setCouponCode(coupon.getCouponCode());
        existing.setDiscountAmount(coupon.getDiscountAmount());
        existing.setExpiryDate(coupon.getExpiryDate());

        return couponsRepository.save(existing);
    }

    @Override
    public void deleteCoupon(Integer couponId) {
        Coupons coupon = getCouponById(couponId);
        couponsRepository.delete(coupon);
    }
    
    @Override
    public Double applyCoupon(String code, Double orderAmount) {
    	Coupons coupon = couponsRepository.findByCouponCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon expired");
        }

        double discount = coupon.getDiscountAmount();

        if (discount > orderAmount) {
            discount = orderAmount;
        }

        return discount;
    }
}