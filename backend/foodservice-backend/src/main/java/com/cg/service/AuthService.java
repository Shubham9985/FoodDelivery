package com.cg.service;

import com.cg.dto.AuthResponseDTO;
import com.cg.dto.LoginDTO;
import com.cg.dto.RegisterDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterDTO dto);
    AuthResponseDTO login(LoginDTO dto);
}