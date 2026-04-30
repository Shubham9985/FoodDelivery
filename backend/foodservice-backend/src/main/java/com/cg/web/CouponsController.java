package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.cg.dto.CouponsDTO;
import com.cg.service.CouponsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
public class CouponsController {

    @Autowired
    private CouponsService service;

    @PostMapping
    public ResponseEntity<CouponsDTO> add(@Valid @RequestBody CouponsDTO dto) {
        return ResponseEntity.ok(service.addCoupon(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponsDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getCouponById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CouponsDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getCouponByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<CouponsDTO>> getAll() {
        return ResponseEntity.ok(service.getAllCoupons());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponsDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CouponsDTO dto) {
        return ResponseEntity.ok(service.updateCoupon(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        service.deleteCoupon(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/apply")
    public ResponseEntity<Double> apply(
            @RequestParam String code,
            @RequestParam Double amount) {
        return ResponseEntity.ok(service.applyCoupon(code, amount));
    }
}