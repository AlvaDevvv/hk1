package com.oreofactory.oreofactory.dto.response;

import lombok.Data;

@Data
public class RegisterResponseDTO {
    private String token;
    private String type = "Bearer";
    private String message = "User registered successfully";
    private String username;
    private String role; // Ahora es String para compatibilidad

    public RegisterResponseDTO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public RegisterResponseDTO(String token, String username, String role, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.message = message;
    }
}