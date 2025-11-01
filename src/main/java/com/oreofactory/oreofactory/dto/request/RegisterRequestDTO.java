package com.oreofactory.oreofactory.dto.request;

import com.oreofactory.oreofactory.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {
    @NotBlank(message = "Username es obligatorio.")
    @Size(min = 3, max = 30, message = "Username 3-30 caracteres permitidos.")
    @Pattern(regexp = "^[a-zA-Z0-9_.]*$", message = "Incluya caracteres especiales para su username.")
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Email(message = "Formato de email inválido.")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min=8, message = "Contraseña debe ser mínimo 8 carácteres.")
    @NotBlank(message = "Contraseña requerida.")
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String branch;
}
