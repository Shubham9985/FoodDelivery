package com.cg.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.CouponsDTO;
import com.cg.entity.Coupons;
import com.cg.exceptions.CouponCodeNotFoundException;
import com.cg.exceptions.CouponException;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.CouponsRepository;

@Service
public class CouponsServiceImpl implements CouponsService {

    @Autowired
    private CouponsRepository couponsRepository;

    // 🔁 DTO → Entity
    private Coupons mapToEntity(CouponsDTO dto) {
        Coupons coupon = new Coupons();
        coupon.setCouponId(dto.getCouponId());
        coupon.setCouponCode(dto.getCouponCode());
        coupon.setDiscountAmount(dto.getDiscountAmount());
        coupon.setExpiryDate(dto.getExpiryDate());
        return coupon;
    }

    @Override
    public Coupons addCoupon(CouponsDTO couponDto) {

        if (couponsRepository.existsByCouponCode(couponDto.getCouponCode())) {
            throw new DuplicateDataException("Coupon code already exists");
        }

        if (couponDto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Cannot add expired coupon");
        }

        Coupons coupon = mapToEntity(couponDto);

        return couponsRepository.save(coupon); // ✅ correct
    }

    @Override
    public Coupons getCouponById(Integer couponId) {
        return couponsRepository.findById(couponId)
                .orElseThrow(() -> new IdNotFoundException("Coupon not found"));
    }

    @Override
    public Coupons getCouponByCode(String couponCode) {
        return couponsRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new CouponCodeNotFoundException("Coupon not found"));
    }

    @Override
    public List<Coupons> getAllCoupons() {
        return couponsRepository.findAll();
    }

    @Override
    public Coupons updateCoupon(Integer couponId, CouponsDTO couponDto) {

        Coupons existing = couponsRepository.findById(couponId)
                .orElseThrow(() -> new IdNotFoundException("Coupon not found"));

        if (!existing.getCouponCode().equals(couponDto.getCouponCode()) &&
                couponsRepository.existsByCouponCode(couponDto.getCouponCode())) {
            throw new DuplicateDataException("Coupon code already exists");
        }

        if (couponDto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Cannot set expired coupon");
        }

        existing.setCouponCode(couponDto.getCouponCode());
        existing.setDiscountAmount(couponDto.getDiscountAmount());
        existing.setExpiryDate(couponDto.getExpiryDate());

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
                .orElseThrow(() -> new CouponCodeNotFoundException("Invalid coupon code"));

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Coupon expired");
        }

        double discount = coupon.getDiscountAmount();

        if (discount > orderAmount) {
            discount = orderAmount;
        }

        return discount;
    }
}