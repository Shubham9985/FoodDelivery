package com.cg.service;

import java.util.List;

import com.cg.dto.DeliveryAddressDTO;

public interface DeliveryAddressService {

    DeliveryAddressDTO createAddress(DeliveryAddressDTO dto);

    List<DeliveryAddressDTO> getAllAddresses();

    DeliveryAddressDTO getAddressById(Integer id);

    DeliveryAddressDTO updateAddress(Integer id, DeliveryAddressDTO dto);

    void deleteAddress(Integer id);

    List<DeliveryAddressDTO> getAddressesByCustomer(Integer customerId);

    DeliveryAddressDTO assignToCustomer(Integer addressId, Integer customerId);
}