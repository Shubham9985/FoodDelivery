package com.cg.service;

import java.util.List;

import com.cg.dto.CouponsDTO;

public interface CouponsService {

	CouponsDTO addCoupon(CouponsDTO dto);

    CouponsDTO getCouponById(Integer id);

    CouponsDTO getCouponByCode(String code);

    List<CouponsDTO> getAllCoupons();

    CouponsDTO updateCoupon(Integer id, CouponsDTO dto);

    void deleteCoupon(Integer id);

    Double applyCoupon(String code, Double orderAmount);
}