package com.cg.service;

import java.util.List;

import com.cg.dto.DeliveryDriverDTO;

public interface DeliveryDriverService {

    DeliveryDriverDTO createDriver(DeliveryDriverDTO dto);

    List<DeliveryDriverDTO> getAllDrivers();

    DeliveryDriverDTO getDriverById(Integer id);

    DeliveryDriverDTO updateDriver(Integer id, DeliveryDriverDTO dto);

    void deleteDriver(Integer id);
}