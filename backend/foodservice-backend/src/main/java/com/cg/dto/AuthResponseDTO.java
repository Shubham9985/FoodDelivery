package com.cg.dto;

public class AuthResponseDTO {

    private String token;
    private String email;
    private String role;
    private String message;
    private Integer customerId;
    private Integer userId;
    private String name;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String email, String role, String message) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public AuthResponseDTO(String token, String email, String role, String message,
                           Integer customerId, Integer userId, String name) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.message = message;
        this.customerId = customerId;
        this.userId = userId;
        this.name = name;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}