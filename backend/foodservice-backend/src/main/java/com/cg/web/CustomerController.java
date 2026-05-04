package com.cg.web;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.CustomerDTO;
import com.cg.dto.DeliveryAddressDTO;
import com.cg.service.CustomerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // CREATE
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        return new ResponseEntity<>(customerService.createCustomer(dto), HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Positive(message = "Customer ID must be a positive integer")
            @PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Positive(message = "Customer ID must be a positive integer")
            @PathVariable Integer id,
            @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @Positive(message = "Customer ID must be a positive integer")
            @PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    // GET ADDRESSES
    @GetMapping("/{id}/addresses")
    public ResponseEntity<Set<DeliveryAddressDTO>> getCustomerAddresses(
            @Positive(message = "Customer ID must be a positive integer")
            @PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(id));
    }
}