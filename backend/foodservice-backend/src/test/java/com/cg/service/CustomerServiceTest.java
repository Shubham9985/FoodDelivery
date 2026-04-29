package com.cg.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.cg.dto.CustomerDTO;
import com.cg.entity.Customer;
import com.cg.exceptions.IdNotFoundException;
import com.cg.repo.CustomerRepository;
import com.cg.service.CustomerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerName("Arpit");
        customer.setCustomerEmail("arpit@gmail.com");
        customer.setCustomerPhone("9999999999");
    }


    @Test
    void createCustomer_positive() {
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerName("Arpit");

        CustomerDTO result = customerService.createCustomer(dto);

        assertEquals("Arpit", result.getCustomerName());
    }

    @Test
    void createCustomer_negative() {
        when(customerRepo.save(any(Customer.class))).thenThrow(new RuntimeException());

        CustomerDTO dto = new CustomerDTO();

        assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));
    }


    @Test
    void getAllCustomers_positive() {
        when(customerRepo.findAll()).thenReturn(List.of(customer));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
    }

    @Test
    void getAllCustomers_negative() {
        when(customerRepo.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertTrue(result.isEmpty());
    }


    @Test
    void getCustomerById_positive() {
        when(customerRepo.findById(1)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1);

        assertEquals("Arpit", result.getCustomerName());
    }

    @Test
    void getCustomerById_negative() {
        when(customerRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> customerService.getCustomerById(1));
    }


    @Test
    void updateCustomer_positive() {
        when(customerRepo.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerName("Updated");

        CustomerDTO result = customerService.updateCustomer(1, dto);

        assertEquals("Updated", result.getCustomerName());
    }

    @Test
    void updateCustomer_negative() {
        when(customerRepo.findById(1)).thenReturn(Optional.empty());

        CustomerDTO dto = new CustomerDTO();

        assertThrows(IdNotFoundException.class,
                () -> customerService.updateCustomer(1, dto));
    }


    @Test
    void deleteCustomer_positive() {
        when(customerRepo.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1));
    }

    @Test
    void deleteCustomer_negative() {
        when(customerRepo.existsById(1)).thenReturn(false);

        assertThrows(IdNotFoundException.class,
                () -> customerService.deleteCustomer(1));
    }


    @Test
    void getCustomerAddresses_positive() {
        customer.setAddresses(new HashSet<>());

        when(customerRepo.findById(1)).thenReturn(Optional.of(customer));

        Set<?> result = customerService.getCustomerAddresses(1);

        assertNotNull(result);
    }

    @Test
    void getCustomerAddresses_negative() {
        when(customerRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> customerService.getCustomerAddresses(1));
    }
}
