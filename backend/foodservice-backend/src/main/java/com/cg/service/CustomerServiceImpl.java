package com.cg.service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.CustomerDTO;
import com.cg.dto.DeliveryAddressDTO;
import com.cg.entity.Customer;
import com.cg.entity.DeliveryAddress;
import com.cg.repo.CustomerRepository;
import com.cg.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    // CREATE 
    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = mapToEntity(dto);
        Customer saved = customerRepo.save(customer);
        return mapToDTO(saved);
    }

    // READ 
    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Integer id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToDTO(customer);
    }

    // UPDATE
    @Override
    public CustomerDTO updateCustomer(Integer id, CustomerDTO dto) {
        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        existing.setCustomerName(dto.getCustomerName());
        existing.setCustomerEmail(dto.getCustomerEmail());
        existing.setCustomerPhone(dto.getCustomerPhone());

        Customer updated = customerRepo.save(existing);
        return mapToDTO(updated);
    }

    //DELETE 
    @Override
    public void deleteCustomer(Integer id) {
        if (!customerRepo.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepo.deleteById(id);
    }

    //RELATION 
    @Override
    public Set<DeliveryAddressDTO> getCustomerAddresses(Integer customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return customer.getAddresses()
                .stream()
                .map(this::mapAddressToDTO)
                .collect(Collectors.toSet());
    }

    // MAPPERS
    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setCustomerEmail(customer.getCustomerEmail());
        dto.setCustomerPhone(customer.getCustomerPhone());

        if (customer.getAddresses() != null) {
            dto.setAddresses(
                customer.getAddresses()
                    .stream()
                    .map(this::mapAddressToDTO)
                    .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setCustomerName(dto.getCustomerName());
        customer.setCustomerEmail(dto.getCustomerEmail());
        customer.setCustomerPhone(dto.getCustomerPhone());
        return customer;
    }

    private DeliveryAddressDTO mapAddressToDTO(DeliveryAddress address) {
        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCustomerId(address.getCustomer().getCustomerId());
        return dto;
    }
}