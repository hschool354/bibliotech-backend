package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRegistrationStatusService registrationStatusService;

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

    @GetMapping("/check-first-login")
    public ResponseEntity<Boolean> checkFirstLogin(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format");
        }

        try {
            Integer userId = jwtTokenProvider.getUserIdFromJWT(token.substring(7));
            boolean isFirstLogin = registrationStatusService.isFirstLogin(userId);
            return ResponseEntity.ok(isFirstLogin);
        } catch (Exception e) {
            throw new BadRequestException("Error checking login status: " + e.getMessage());
        }
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfileData profileData
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format");
        }

        try {
            Integer userId = jwtTokenProvider.getUserIdFromJWT(token.substring(7));
            registrationStatusService.completeProfile(userId, profileData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BadRequestException("Error completing profile: " + e.getMessage());
        }
    }
}