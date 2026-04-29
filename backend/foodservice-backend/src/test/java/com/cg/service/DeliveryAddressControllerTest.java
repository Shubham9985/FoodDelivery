package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.cg.dto.DeliveryAddressDTO;
import com.cg.exceptions.IdNotFoundException;
import com.cg.service.DeliveryAddressService;
import com.cg.web.DeliveryAddressController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DeliveryAddressControllerTest {

    @Mock
    private DeliveryAddressService addressService;

    @InjectMocks
    private DeliveryAddressController controller;

    private DeliveryAddressDTO dto;

    @BeforeEach
    void setUp() {

        dto = new DeliveryAddressDTO();
        dto.setAddressId(1);
        dto.setAddressLine1("House No. 602");
        dto.setAddressLine2("Haven Street");
        dto.setCity("Gotham");
        dto.setState("New York");
        dto.setPostalCode("123456");
        dto.setCustomerId(101);
    }

    // ---------------- CREATE ----------------

    @Test
    void testCreateAddress() {

        when(addressService.createAddress(dto))
                .thenReturn(dto);

        ResponseEntity<DeliveryAddressDTO> response =
                controller.createAddress(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Gotham", response.getBody().getCity());

        verify(addressService).createAddress(dto);
    }

    @Test
    void testCreateAddress_Negative() {

        when(addressService.createAddress(dto))
                .thenThrow(new IdNotFoundException("Customer not found"));

        assertThrows(IdNotFoundException.class,
                () -> controller.createAddress(dto));
    }

    // ---------------- GET ALL ----------------

    @Test
    void testGetAllAddresses() {

        when(addressService.getAllAddresses())
                .thenReturn(Arrays.asList(dto));

        ResponseEntity<List<DeliveryAddressDTO>> response =
                controller.getAllAddresses();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(addressService).getAllAddresses();
    }

    // ---------------- GET BY ID ----------------

    @Test
    void testGetAddressById() {

        when(addressService.getAddressById(1))
                .thenReturn(dto);

        ResponseEntity<DeliveryAddressDTO> response =
                controller.getAddressById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getAddressId());

        verify(addressService).getAddressById(1);
    }

    @Test
    void testGetAddressById_Negative() {

        when(addressService.getAddressById(1))
                .thenThrow(new IdNotFoundException("Address not found"));

        assertThrows(IdNotFoundException.class,
                () -> controller.getAddressById(1));
    }

    // ---------------- UPDATE ----------------

    @Test
    void testUpdateAddress() {

        when(addressService.updateAddress(1, dto))
                .thenReturn(dto);

        ResponseEntity<DeliveryAddressDTO> response =
                controller.updateAddress(1, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Gotham", response.getBody().getCity());

        verify(addressService).updateAddress(1, dto);
    }

    @Test
    void testUpdateAddress_Negative() {

        when(addressService.updateAddress(1, dto))
                .thenThrow(new IdNotFoundException("Address not found"));

        assertThrows(IdNotFoundException.class,
                () -> controller.updateAddress(1, dto));
    }

    // ---------------- DELETE ----------------

    @Test
    void testDeleteAddress() {

        doNothing().when(addressService).deleteAddress(1);

        ResponseEntity<String> response =
                controller.deleteAddress(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Address deleted successfully",
                response.getBody());

        verify(addressService).deleteAddress(1);
    }

    @Test
    void testDeleteAddress_Negative() {

        doThrow(new IdNotFoundException("Address not found"))
                .when(addressService).deleteAddress(1);

        assertThrows(IdNotFoundException.class,
                () -> controller.deleteAddress(1));
    }

    // ---------------- GET BY CUSTOMER ----------------

    @Test
    void testGetByCustomer() {

        when(addressService.getAddressesByCustomer(101))
                .thenReturn(Arrays.asList(dto));

        ResponseEntity<List<DeliveryAddressDTO>> response =
                controller.getByCustomer(101);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(addressService).getAddressesByCustomer(101);
    }

    @Test
    void testGetByCustomer_Negative() {

        when(addressService.getAddressesByCustomer(101))
                .thenThrow(new IdNotFoundException("Customer not found"));

        assertThrows(IdNotFoundException.class,
                () -> controller.getByCustomer(101));
    }

    // ---------------- ASSIGN CUSTOMER ----------------

    @Test
    void testAssignCustomer() {

        when(addressService.assignToCustomer(1, 101))
                .thenReturn(dto);

        ResponseEntity<DeliveryAddressDTO> response =
                controller.assignCustomer(1, 101);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(101, response.getBody().getCustomerId());

        verify(addressService).assignToCustomer(1, 101);
    }

    @Test
    void testAssignCustomer_Negative() {

        when(addressService.assignToCustomer(1, 101))
                .thenThrow(new IdNotFoundException("Address or Customer not found"));

        assertThrows(IdNotFoundException.class,
                () -> controller.assignCustomer(1, 101));
    }
}