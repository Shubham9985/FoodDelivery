package com.cg.service;

import java.util.List;

import com.cg.entity.Coupons;

public interface CouponsService {

    Coupons addCoupon(Coupons coupon);

    Coupons getCouponById(Integer couponId);

    Coupons getCouponByCode(String couponCode);

    List<Coupons> getAllCoupons();

    Coupons updateCoupon(Integer couponId, Coupons coupon);

    void deleteCoupon(Integer couponId);
    
    Double applyCoupon(String code, Double orderAmount);
}