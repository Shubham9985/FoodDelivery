package com.cg.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.dto.AuthRequest;
import com.cg.dto.AuthResponse;
import com.cg.dto.RegisterDTO;
import com.cg.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;
    
 // Login to Account
 	@PostMapping("/login")
     public AuthResponse login(@RequestBody AuthRequest request) {
         String token = service.login(request.getEmail(), request.getPassword());
         return new AuthResponse(token);
     }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(service.register(dto));
    }
}