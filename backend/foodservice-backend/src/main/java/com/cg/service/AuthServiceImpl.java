package com.cg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.dto.RegisterDTO;
import com.cg.entity.Customer;
import com.cg.entity.User;
import com.cg.enums.Role;
import com.cg.exceptions.DuplicateDataException;
import com.cg.repo.CustomerRepository;
import com.cg.repo.UserRepository;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public String register(RegisterDTO dto) {

        // Check duplicate email
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateDataException("Email already registered");
        }

        // Create User
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // later encrypt
        user.setRole(Role.CUSTOMER);
        user.setIsActive(true);

        user = userRepo.save(user);

        // Create Customer
        Customer customer = new Customer();
        customer.setCustomerName(dto.getName());
        customer.setCustomerPhone(dto.getPhone());
        customer.setUser(user);

        customerRepo.save(customer);

        return "User registered successfully";
    }
}