package com.cg.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.dto.CouponsDTO;
import com.cg.entity.Coupons;
import com.cg.exceptions.CouponCodeNotFoundException;
import com.cg.exceptions.CouponException;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.CouponsRepository;

@Service
@Transactional
public class CouponsServiceImpl implements CouponsService {

    @Autowired
    private CouponsRepository repo;

    // 🔄 Mapping
    private Coupons mapToEntity(CouponsDTO dto) {
        Coupons c = new Coupons();
        c.setCouponId(dto.getCouponId());
        c.setCouponCode(dto.getCouponCode());
        c.setDiscountAmount(dto.getDiscountAmount());
        c.setExpiryDate(dto.getExpiryDate());
        return c;
    }

    private CouponsDTO mapToDTO(Coupons c) {
        return new CouponsDTO(
                c.getCouponId(),
                c.getCouponCode(),
                c.getDiscountAmount(),
                c.getExpiryDate()
        );
    }

    // ➕ Add
    @Override
    public CouponsDTO addCoupon(CouponsDTO dto) {

        if (repo.existsByCouponCode(dto.getCouponCode())) {
            throw new DuplicateDataException("Coupon already exists");
        }

        if (dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Cannot add expired coupon");
        }

        Coupons saved = repo.save(mapToEntity(dto));
        return mapToDTO(saved);
    }

    // 🔍 Get by ID
    @Override
    public CouponsDTO getCouponById(Integer id) {
        Coupons c = repo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Coupon not found"));
        return mapToDTO(c);
    }

    // 🔍 Get by Code
    @Override
    public CouponsDTO getCouponByCode(String code) {
        Coupons c = repo.findByCouponCode(code)
                .orElseThrow(() -> new CouponCodeNotFoundException("Invalid coupon code"));
        return mapToDTO(c);
    }

    // 📄 Get All
    @Override
    public List<CouponsDTO> getAllCoupons() {
        return repo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✏️ Update
    @Override
    public CouponsDTO updateCoupon(Integer id, CouponsDTO dto) {

        Coupons existing = repo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Coupon not found"));

        if (!existing.getCouponCode().equals(dto.getCouponCode()) &&
                repo.existsByCouponCode(dto.getCouponCode())) {
            throw new DuplicateDataException("Coupon code already exists");
        }

        if (dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Invalid expiry date");
        }

        existing.setCouponCode(dto.getCouponCode());
        existing.setDiscountAmount(dto.getDiscountAmount());
        existing.setExpiryDate(dto.getExpiryDate());

        return mapToDTO(repo.save(existing));
    }

    // ❌ Delete
    @Override
    public void deleteCoupon(Integer id) {
        Coupons c = repo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Coupon not found"));
        repo.delete(c);
    }

    // 💰 Apply Coupon
    @Override
    public Double applyCoupon(String code, Double orderAmount) {

        if (orderAmount <= 0) {
            throw new CouponException("Invalid order amount");
        }

        Coupons c = repo.findByCouponCode(code)
                .orElseThrow(() -> new CouponCodeNotFoundException("Invalid coupon"));

        if (c.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponException("Coupon expired");
        }

        return Math.min(c.getDiscountAmount(), orderAmount);
    }
}