package com.cg.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.entity.Coupons;

@Repository
public interface CouponsRepository extends JpaRepository<Coupons, Integer> {

    // Find coupon by code
    Optional<Coupons> findByCouponCode(String couponCode);

    // Check if coupon exists
    boolean existsByCouponCode(String couponCode);
}