package com.cg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.dto.AuthResponseDTO;
import com.cg.dto.LoginDTO;
import com.cg.dto.RegisterDTO;
import com.cg.entity.Customer;
import com.cg.entity.User;
import com.cg.enums.Role;
import com.cg.exceptions.DuplicateDataException;
import com.cg.exceptions.UnauthorizedException;
import com.cg.repo.CustomerRepository;
import com.cg.repo.UserRepository;
import com.cg.security.JwtUtil;
import com.cg.service.AuthService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO register(RegisterDTO dto) {

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateDataException("Email already registered");
        }

        Role role = (dto.getRole() != null) ? dto.getRole() : Role.CUSTOMER;

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setIsActive(true);

        user = userRepo.save(user);

        if (role == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setCustomerName(dto.getName());
            customer.setCustomerEmail(dto.getEmail());
            customer.setCustomerPhone(dto.getPhone());
            customer.setUser(user);
            customerRepo.save(customer);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getRole().name(),
                role == Role.CUSTOMER ? "Customer registered successfully"
                                      : "Admin registered successfully");
    }

    @Override
    public AuthResponseDTO login(LoginDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is inactive");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name(), "Login successful");
    }
}