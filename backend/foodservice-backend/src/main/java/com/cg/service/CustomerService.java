package com.cg.service;

import java.util.List;
import java.util.Set;

import com.cg.dto.CustomerDTO;
import com.cg.dto.DeliveryAddressDTO;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO dto);

    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Integer id);

    CustomerDTO updateCustomer(Integer id, CustomerDTO dto);

    void deleteCustomer(Integer id);

    Set<DeliveryAddressDTO> getCustomerAddresses(Integer customerId);
}
