package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.dto.request.*;
import com.oreofactory.oreofactory.dto.response.*;
import com.oreofactory.oreofactory.model.entity.User;
import com.oreofactory.oreofactory.model.enums.Role;
import com.oreofactory.oreofactory.repository.UserRepository;
import com.oreofactory.oreofactory.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validar y convertir el string del role a enum
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.getRole());
        }

        if (Role.ROLE_BRANCH.equals(role) && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new RuntimeException("Branch is required for ROLE_BRANCH");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .branch(request.getBranch())
                .build();

        User savedUser = userRepository.save(user);

        var jwt = jwtUtil.generateToken(savedUser.getUsername());
        return new RegisterResponseDTO(jwt, savedUser.getUsername(), savedUser.getRole().name());
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwt = jwtUtil.generateToken(user.getUsername());
        return new LoginResponseDTO(
                jwt,
                user.getUsername(),
                user.getRole().name(),
                user.getBranch()
        );
    }
}