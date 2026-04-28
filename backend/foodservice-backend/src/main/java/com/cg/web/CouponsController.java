package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cg.entity.Coupons;
import com.cg.service.CouponsService;

@RestController
@RequestMapping("/api/coupons")
public class CouponsController {

    @Autowired
    private CouponsService couponService;

    @PostMapping
    public ResponseEntity<Coupons> createCoupon(@RequestBody Coupons coupon) {
        Coupons savedCoupon = couponService.addCoupon(coupon);
        return new ResponseEntity<>(savedCoupon, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupons> getCouponById(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @GetMapping
    public ResponseEntity<List<Coupons>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/apply/{code}")
    public ResponseEntity<String> applyCoupon(
            @PathVariable String code,
            @RequestParam double orderAmount) {

        double discount = couponService.applyCoupon(code, orderAmount);

        return ResponseEntity.ok(
                "Coupon applied! Discount: " + discount
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupons> updateCoupon(
            @PathVariable Integer id,
            @RequestBody Coupons coupon) {

        return ResponseEntity.ok(
                couponService.updateCoupon(id, coupon)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Integer id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully");
    }
}