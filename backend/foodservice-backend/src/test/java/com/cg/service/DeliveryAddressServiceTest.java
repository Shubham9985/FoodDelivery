package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.cg.dto.DeliveryAddressDTO;
import com.cg.entity.Customer;
import com.cg.entity.DeliveryAddress;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.CustomerRepository;
import com.cg.repo.DeliveryAddressRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryAddressServiceTest {

    @Mock
    private DeliveryAddressRepository addressRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private DeliveryAddressServiceImpl service;

    private DeliveryAddress address;
    private DeliveryAddressDTO dto;
    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setCustomerId(101);

        address = new DeliveryAddress();
        address.setAddressId(1);
        address.setAddressLine1("House No. 602");
        address.setAddressLine2("Haven Street");
        address.setCity("Gotham");
        address.setState("New York");
        address.setPostalCode("123456");
        address.setCustomer(customer);

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

        when(customerRepo.findById(101))
                .thenReturn(Optional.of(customer));

        when(addressRepo.save(any(DeliveryAddress.class)))
                .thenReturn(address);

        DeliveryAddressDTO result = service.createAddress(dto);

        assertNotNull(result);
        assertEquals("Gotham", result.getCity());
        assertEquals(101, result.getCustomerId());

        verify(customerRepo).findById(101);
        verify(addressRepo).save(any(DeliveryAddress.class));
    }

    @Test
    void testCreateAddress_CustomerNotFound() {

        when(customerRepo.findById(101))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.createAddress(dto));

        verify(addressRepo, never()).save(any());
    }

    // ---------------- READ ALL ----------------

    @Test
    void testGetAllAddresses() {

        when(addressRepo.findAll())
                .thenReturn(Arrays.asList(address));

        List<DeliveryAddressDTO> result =
                service.getAllAddresses();

        assertEquals(1, result.size());
        assertEquals("Gotham", result.get(0).getCity());
    }

    // ---------------- READ BY ID ----------------

    @Test
    void testGetAddressById() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.of(address));

        DeliveryAddressDTO result =
                service.getAddressById(1);

        assertEquals(1, result.getAddressId());
        assertEquals("Gotham", result.getCity());
    }

    @Test
    void testGetAddressById_NotFound() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.getAddressById(1));
    }

    // ---------------- UPDATE ----------------

    @Test
    void testUpdateAddress() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.of(address));

        when(addressRepo.save(any(DeliveryAddress.class)))
                .thenReturn(address);

        dto.setCity("Metropolis");

        DeliveryAddressDTO result =
                service.updateAddress(1, dto);

        assertNotNull(result);

        verify(addressRepo).findById(1);
        verify(addressRepo).save(any(DeliveryAddress.class));
    }

    @Test
    void testUpdateAddress_NotFound() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.updateAddress(1, dto));

        verify(addressRepo, never()).save(any());
    }

    // ---------------- DELETE ----------------

    @Test
    void testDeleteAddress() {

        when(addressRepo.existsById(1))
                .thenReturn(true);

        doNothing().when(addressRepo).deleteById(1);

        service.deleteAddress(1);

        verify(addressRepo).deleteById(1);
    }

    @Test
    void testDeleteAddress_NotFound() {

        when(addressRepo.existsById(1))
                .thenReturn(false);

        assertThrows(IdNotFoundException.class,
                () -> service.deleteAddress(1));

        verify(addressRepo, never()).deleteById(anyInt());
    }

    // ---------------- GET BY CUSTOMER ----------------

    @Test
    void testGetAddressesByCustomer() {

        when(addressRepo.findByCustomerCustomerId(101))
                .thenReturn(Arrays.asList(address));

        List<DeliveryAddressDTO> result =
                service.getAddressesByCustomer(101);

        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getCustomerId());
    }

    // ---------------- ASSIGN CUSTOMER ----------------

    @Test
    void testAssignToCustomer() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.of(address));

        when(customerRepo.findById(101))
                .thenReturn(Optional.of(customer));

        when(addressRepo.save(any(DeliveryAddress.class)))
                .thenReturn(address);

        DeliveryAddressDTO result =
                service.assignToCustomer(1, 101);

        assertEquals(101, result.getCustomerId());

        verify(addressRepo).save(any(DeliveryAddress.class));
    }

    @Test
    void testAssignToCustomer_AddressNotFound() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.assignToCustomer(1, 101));
    }

    @Test
    void testAssignToCustomer_CustomerNotFound() {

        when(addressRepo.findById(1))
                .thenReturn(Optional.of(address));

        when(customerRepo.findById(101))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> service.assignToCustomer(1, 101));
    }
}