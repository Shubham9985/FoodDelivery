package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.service.DeliveryDriverService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/drivers")
@Validated
public class DeliveryDriverController {

    @Autowired
    private DeliveryDriverService driverService;

    // CREATE
    @PostMapping
    public ResponseEntity<DeliveryDriverDTO> createDriver(@Valid @RequestBody DeliveryDriverDTO dto) {
        return new ResponseEntity<>(driverService.createDriver(dto), HttpStatus.CREATED);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<DeliveryDriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDriverDTO> getDriverById(
            @Positive(message = "Driver ID must be positive")
            @PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryDriverDTO> updateDriver(
            @Positive(message = "Driver ID must be positive")
            @PathVariable Integer id,
            @Valid @RequestBody DeliveryDriverDTO dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(
            @Positive(message = "Driver ID must be positive")
            @PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok("Driver deleted successfully");
    }
}