package com.oreofactory.oreofactory.dto.request;

import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @NotBlank(message = "Username es obligatorio.")
    @Size(min = 3, max = 30, message = "Username 3-30 caracteres permitidos.")
    @Pattern(regexp = "^[a-zA-Z0-9_.]*$", message = "Incluya caracteres especiales para su username.")
    @Column(nullable = false)
    private String username;

    @Size(min=8, message = "Contraseña debe ser mínimo 8 carácteres.")
    @NotBlank(message = "Contraseña requerida.")
    @Column(nullable = false)
    private String password;
}
