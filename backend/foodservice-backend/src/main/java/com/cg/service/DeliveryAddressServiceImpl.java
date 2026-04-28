package com.cg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.dto.DeliveryAddressDTO;
import com.cg.entity.Customer;
import com.cg.entity.DeliveryAddress;
import com.cg.repo.CustomerRepository;
import com.cg.repo.DeliveryAddressRepository;
import com.cg.service.DeliveryAddressService;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    @Autowired
    private DeliveryAddressRepository addressRepo;

    @Autowired
    private CustomerRepository customerRepo;

    // CREATE
    @Override
    public DeliveryAddressDTO createAddress(DeliveryAddressDTO dto) {
        DeliveryAddress address = mapToEntity(dto);

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepo.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            address.setCustomer(customer);
        }

        DeliveryAddress saved = addressRepo.save(address);
        return mapToDTO(saved);
    }

    // READ ALL
    @Override
    public List<DeliveryAddressDTO> getAllAddresses() {
        return addressRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    @Override
    public DeliveryAddressDTO getAddressById(Integer id) {
        DeliveryAddress address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return mapToDTO(address);
    }

    // UPDATE
    @Override
    public DeliveryAddressDTO updateAddress(Integer id, DeliveryAddressDTO dto) {
        DeliveryAddress existing = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        existing.setAddressLine1(dto.getAddressLine1());
        existing.setAddressLine2(dto.getAddressLine2());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPostalCode(dto.getPostalCode());

        DeliveryAddress updated = addressRepo.save(existing);
        return mapToDTO(updated);
    }

    // DELETE
    @Override
    public void deleteAddress(Integer id) {
        if (!addressRepo.existsById(id)) {
            throw new RuntimeException("Address not found");
        }
        addressRepo.deleteById(id);
    }

    // GET BY CUSTOMER
    @Override
    public List<DeliveryAddressDTO> getAddressesByCustomer(Integer customerId) {
        return addressRepo.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ASSIGN CUSTOMER
    @Override
    public DeliveryAddressDTO assignToCustomer(Integer addressId, Integer customerId) {
        DeliveryAddress address = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        address.setCustomer(customer);

        return mapToDTO(addressRepo.save(address));
    }

    // ---------- MAPPERS ----------
    private DeliveryAddressDTO mapToDTO(DeliveryAddress address) {
        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());

        if (address.getCustomer() != null) {
            dto.setCustomerId(address.getCustomer().getCustomerId());
        }

        return dto;
    }

    private DeliveryAddress mapToEntity(DeliveryAddressDTO dto) {
        DeliveryAddress address = new DeliveryAddress();
        address.setAddressId(dto.getAddressId());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }
}