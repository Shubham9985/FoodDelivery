package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.DeliveryAddressDTO;
import com.cg.service.DeliveryAddressService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/addresses")
@Validated
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService addressService;

    // CREATE
    @PostMapping
    public ResponseEntity<DeliveryAddressDTO> createAddress(@Valid @RequestBody DeliveryAddressDTO dto) {
        return new ResponseEntity<>(addressService.createAddress(dto), HttpStatus.CREATED);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<DeliveryAddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAddressDTO> getAddressById(
            @Positive(message = "Address ID must be positive")
            @PathVariable Integer id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAddressDTO> updateAddress(
            @Positive(message = "Address ID must be positive")
            @PathVariable Integer id,
            @Valid @RequestBody DeliveryAddressDTO dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(
            @Positive(message = "Address ID must be positive")
            @PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Address deleted successfully");
    }

    // GET BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryAddressDTO>> getByCustomer(
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.getAddressesByCustomer(customerId));
    }

    // ASSIGN CUSTOMER (PUT — re-assignment is idempotent)
    @PutMapping("/{addressId}/assign/{customerId}")
    public ResponseEntity<DeliveryAddressDTO> assignCustomer(
            @Positive(message = "Address ID must be positive")
            @PathVariable Integer addressId,
            @Positive(message = "Customer ID must be positive")
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.assignToCustomer(addressId, customerId));
    }
}