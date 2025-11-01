package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.dto.request.LoginRequestDTO;
import com.oreofactory.oreofactory.dto.request.RegisterRequestDTO;
import com.oreofactory.oreofactory.dto.response.LoginResponseDTO;
import com.oreofactory.oreofactory.dto.response.RegisterResponseDTO;
import com.oreofactory.oreofactory.exception.exceptions.UserAlreadyExistsException;
import com.oreofactory.oreofactory.exception.exceptions.UsernameAlreadyExistsException;
import com.oreofactory.oreofactory.model.entity.User;
import com.oreofactory.oreofactory.model.enums.Role;
import com.oreofactory.oreofactory.repository.UserRepository;
import com.oreofactory.oreofactory.security.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;

    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        // Validar unicidad de username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        // Validar unicidad de email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        // Generar token de verificación
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : Role.STUDENT)
                .verificationToken(verificationToken)
                .build();

        User savedUser = userRepository.save(user);

        // Publicar evento de registro de usuario
        eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser, verificationToken));

        return modelMapper.map(savedUser, UserResponseDTO.class);
    }



    // Validaciones de usuario único
    // Hash de contraseña
    // Crear usuario con ID único
    // Guardar en base de datos
    // Publicar evento de registro


    public LoginResponseDTO login(LoginRequestDTO request) {
    // Validar credenciales
    // Generar JWT
    // Retornar respuesta
    }
}
