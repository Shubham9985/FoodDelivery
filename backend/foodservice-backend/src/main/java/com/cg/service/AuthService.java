package com.cg.service;

import com.cg.dto.RegisterDTO;

public interface AuthService {

    public String register(RegisterDTO dto);
    
    public String login(String email, String password);
}