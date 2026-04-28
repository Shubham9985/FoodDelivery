package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.service.DeliveryDriverService;

@RestController
@RequestMapping("/drivers")
public class DeliveryDriverController {

    @Autowired
    private DeliveryDriverService driverService;

    // CREATE
    @PostMapping
    public ResponseEntity<DeliveryDriverDTO> createDriver(@RequestBody DeliveryDriverDTO dto) {
        return ResponseEntity.ok(driverService.createDriver(dto));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<DeliveryDriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDriverDTO> getDriverById(@PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryDriverDTO> updateDriver(
            @PathVariable Integer id,
            @RequestBody DeliveryDriverDTO dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok("Driver deleted successfully");
    }
}