package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.DeliveryAddressDTO;
import com.cg.service.DeliveryAddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/addresses")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService addressService;

    // CREATE
    @PostMapping
    public ResponseEntity<DeliveryAddressDTO> createAddress(@Valid @RequestBody DeliveryAddressDTO dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<DeliveryAddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAddressDTO> getAddressById(@PathVariable Integer id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAddressDTO> updateAddress(
            @PathVariable Integer id,
           @Valid @RequestBody DeliveryAddressDTO dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Address deleted successfully");
    }

    // GET BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryAddressDTO>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.getAddressesByCustomer(customerId));
    }

    // ASSIGN CUSTOMER
    @PostMapping("/{addressId}/assign/{customerId}")
    public ResponseEntity<DeliveryAddressDTO> assignCustomer(
            @PathVariable Integer addressId,
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.assignToCustomer(addressId, customerId));
    }
}
