package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.cg.dto.DeliveryDriverDTO;
import com.cg.entity.DeliveryDriver;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.DeliveryDriverRepository;
import com.cg.service.DeliveryDriverServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryDriverServiceTest {

    @Mock
    private DeliveryDriverRepository driverRepo;

    @InjectMocks
    private DeliveryDriverServiceImpl driverService;

    private DeliveryDriver driver;

    @BeforeEach
    void setUp() {
        driver = new DeliveryDriver();
        driver.setDriverId(1);
        driver.setDriverName("Rahul");
        driver.setDriverPhone("9999999999");
        driver.setDriverVehicle("Bike");
    }

    

    @Test
    void createDriver_positive() {
        when(driverRepo.save(any(DeliveryDriver.class))).thenReturn(driver);

        DeliveryDriverDTO dto = new DeliveryDriverDTO();
        dto.setDriverName("Rahul");

        DeliveryDriverDTO result = driverService.createDriver(dto);

        assertEquals("Rahul", result.getDriverName());
    }

    @Test
    void createDriver_negative() {
        when(driverRepo.save(any(DeliveryDriver.class))).thenThrow(new RuntimeException());

        DeliveryDriverDTO dto = new DeliveryDriverDTO();

        assertThrows(RuntimeException.class,
                () -> driverService.createDriver(dto));
    }

    

    @Test
    void getAllDrivers_positive() {
        when(driverRepo.findAll()).thenReturn(List.of(driver));

        List<DeliveryDriverDTO> result = driverService.getAllDrivers();

        assertEquals(1, result.size());
    }

    @Test
    void getAllDrivers_negative() {
        when(driverRepo.findAll()).thenReturn(Collections.emptyList());

        List<DeliveryDriverDTO> result = driverService.getAllDrivers();

        assertTrue(result.isEmpty());
    }

    

    @Test
    void getDriverById_positive() {
        when(driverRepo.findById(1)).thenReturn(Optional.of(driver));

        DeliveryDriverDTO result = driverService.getDriverById(1);

        assertEquals("Rahul", result.getDriverName());
    }

    @Test
    void getDriverById_negative() {
        when(driverRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> driverService.getDriverById(1));
    }

   

    @Test
    void updateDriver_positive() {
        when(driverRepo.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepo.save(any(DeliveryDriver.class))).thenReturn(driver);

        DeliveryDriverDTO dto = new DeliveryDriverDTO();
        dto.setDriverName("Updated");

        DeliveryDriverDTO result = driverService.updateDriver(1, dto);

        assertEquals("Updated", result.getDriverName());
    }

    @Test
    void updateDriver_negative() {
        when(driverRepo.findById(1)).thenReturn(Optional.empty());

        DeliveryDriverDTO dto = new DeliveryDriverDTO();

        assertThrows(IdNotFoundException.class,
                () -> driverService.updateDriver(1, dto));
    }


    @Test
    void deleteDriver_positive() {
        when(driverRepo.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> driverService.deleteDriver(1));
    }

    @Test
    void deleteDriver_negative() {
        when(driverRepo.existsById(1)).thenReturn(false);

        assertThrows(IdNotFoundException.class,
                () -> driverService.deleteDriver(1));
    }
}
