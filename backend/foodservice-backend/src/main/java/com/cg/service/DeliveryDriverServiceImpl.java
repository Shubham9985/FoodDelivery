package com.cg.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.entity.DeliveryDriver;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.DeliveryDriverRepository;
import com.cg.service.DeliveryDriverService;

@Service
public class DeliveryDriverServiceImpl implements DeliveryDriverService {

    @Autowired
    private DeliveryDriverRepository driverRepo;

    // CREATE
    @Override
    public DeliveryDriverDTO createDriver(DeliveryDriverDTO dto) {
        DeliveryDriver driver = mapToEntity(dto);
        DeliveryDriver saved = driverRepo.save(driver);
        return mapToDTO(saved);
    }

    // READ ALL
    @Override
    public List<DeliveryDriverDTO> getAllDrivers() {
        return driverRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    @Override
    public DeliveryDriverDTO getDriverById(Integer id) {
        DeliveryDriver driver = driverRepo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Driver not found"));
        return mapToDTO(driver);
    }

    // UPDATE
    @Override
    public DeliveryDriverDTO updateDriver(Integer id, DeliveryDriverDTO dto) {
        DeliveryDriver existing = driverRepo.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Driver not found"));

        existing.setDriverName(dto.getDriverName());
        existing.setDriverPhone(dto.getDriverPhone());
        existing.setDriverVehicle(dto.getDriverVehicle());

        DeliveryDriver updated = driverRepo.save(existing);
        return mapToDTO(updated);
    }

    // DELETE
    @Override
    public void deleteDriver(Integer id) {
        if (!driverRepo.existsById(id)) {
            throw new IdNotFoundException("Driver not found");
        }
        driverRepo.deleteById(id);
    }

    // ---------- MAPPERS ----------
    private DeliveryDriverDTO mapToDTO(DeliveryDriver driver) {
        DeliveryDriverDTO dto = new DeliveryDriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName());
        dto.setDriverPhone(driver.getDriverPhone());
        dto.setDriverVehicle(driver.getDriverVehicle());
        return dto;
    }

    private DeliveryDriver mapToEntity(DeliveryDriverDTO dto) {
        DeliveryDriver driver = new DeliveryDriver();
        driver.setDriverId(dto.getDriverId());
        driver.setDriverName(dto.getDriverName());
        driver.setDriverPhone(dto.getDriverPhone());
        driver.setDriverVehicle(dto.getDriverVehicle());
        return driver;
    }
}