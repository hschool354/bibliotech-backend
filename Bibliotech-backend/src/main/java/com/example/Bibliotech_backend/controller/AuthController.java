package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}