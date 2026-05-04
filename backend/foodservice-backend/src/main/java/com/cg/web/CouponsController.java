package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cg.dto.CouponsDTO;
import com.cg.service.CouponsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/coupons")
@Validated
public class CouponsController {

    @Autowired
    private CouponsService service;

    @PostMapping
    public ResponseEntity<CouponsDTO> add(@Valid @RequestBody CouponsDTO dto) {
        return new ResponseEntity<>(service.addCoupon(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponsDTO> getById(
            @Positive(message = "Coupon ID must be positive")
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.getCouponById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponsDTO> getByCode(
            @NotBlank(message = "Coupon code must not be blank")
            @PathVariable String code) {
        return ResponseEntity.ok(service.getCouponByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<CouponsDTO>> getAll() {
        return ResponseEntity.ok(service.getAllCoupons());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponsDTO> update(
            @Positive(message = "Coupon ID must be positive")
            @PathVariable Integer id,
            @Valid @RequestBody CouponsDTO dto) {
        return ResponseEntity.ok(service.updateCoupon(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Positive(message = "Coupon ID must be positive")
            @PathVariable Integer id) {
        service.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully");
    }

    @GetMapping("/apply")
    public ResponseEntity<Double> apply(
            @NotBlank(message = "Coupon code must not be blank")
            @RequestParam String code,
            @Positive(message = "Order amount must be greater than 0")
            @RequestParam Double amount) {
        return ResponseEntity.ok(service.applyCoupon(code, amount));
    }
}