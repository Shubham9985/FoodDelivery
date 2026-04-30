package com.cg.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.dto.RegisterDTO;
import com.cg.entity.Customer;
import com.cg.entity.User;
import com.cg.enums.Role;
import com.cg.repo.CustomerRepository;
import com.cg.repo.UserRepository;

@RestController
@RequestMapping("auth")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterDTO dto) {

	    User user = new User();
	    user.setEmail(dto.getEmail());
	    user.setPassword(dto.getPassword());
	    user.setRole(Role.CUSTOMER);

	    user = userRepository.save(user);

	    Customer customer = new Customer();
	    customer.setCustomerName(dto.getName());
	    customer.setCustomerEmail(dto.getEmail());
	    customer.setCustomerPhone(dto.getPhone());
	    customer.setUser(user);

	    customerRepository.save(customer);

	    return ResponseEntity.ok("User and Customer registered");
	}
	

}
