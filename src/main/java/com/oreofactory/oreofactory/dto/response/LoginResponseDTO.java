package com.oreofactory.oreofactory.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role; // Ahora es String para compatibilidad
    private String branch;

    public LoginResponseDTO(String token, String username, String role, String branch) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.branch = branch;
    }
}