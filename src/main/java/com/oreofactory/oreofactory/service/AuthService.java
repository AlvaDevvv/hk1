package com.oreofactory.oreofactory.service;

import com.oreofactory.oreofactory.dto.request.RegisterRequestDTO;
import com.oreofactory.oreofactory.dto.response.RegisterResponseDTO;
import com.oreofactory.oreofactory.dto.request.LoginRequestDTO;
import com.oreofactory.oreofactory.model.entity.User;
import com.oreofactory.oreofactory.repository.UserRepository;
import com.oreofactory.oreofactory.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if ("ROLE_BRANCH".equals(request.getRole()) && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new RuntimeException("Branch is required for ROLE_BRANCH");
        }

        User user = modelMapper.map(request, User.class);
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        var jwt = jwtUtil.generateToken(user.getUsername());
        return new RegisterResponseDTO(jwt);
    }

    public RegisterResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var jwt = jwtUtil.generateToken(request.getUsername());
        return new RegisterResponseDTO(jwt);
    }
}