package com.oreofactory.oreofactory.controller;

import com.oreofactory.oreofactory.dto.request.LoginRequestDTO;
import com.oreofactory.oreofactory.dto.request.RegisterRequestDTO;
import com.oreofactory.oreofactory.dto.response.LoginResponseDTO;
import com.oreofactory.oreofactory.dto.response.RegisterResponseDTO;
import com.oreofactory.oreofactory.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}