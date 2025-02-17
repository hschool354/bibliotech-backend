package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
            logger.error("Invalid token format");
            throw new BadRequestException("Invalid token format");
        }

        try {
            String rawToken = token.substring(7);
            logger.debug("Token received: " + rawToken);
            logger.debug("Profile data received: " + profileData);  // Add this line

            Integer userId = jwtTokenProvider.getUserIdFromJWT(rawToken);
            logger.debug("User ID extracted: " + userId);

            registrationStatusService.completeProfile(userId, profileData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error completing profile", e);
            throw new BadRequestException("Error completing profile: " + e.getMessage());
        }
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> checkAdminStatus(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            logger.error("Invalid token format");
            throw new BadRequestException("Invalid token format");
        }

        try {
            String rawToken = token.substring(7);
            logger.debug("Token received for admin check: " + rawToken);

            // Get user ID from JWT token
            Integer userId = jwtTokenProvider.getUserIdFromJWT(rawToken);
            logger.debug("User ID extracted for admin check: " + userId);

            // Get user from repository
            Users user = authService.getUserById(userId);

            if (user == null) {
                throw new BadRequestException("User not found");
            }

            logger.debug("Admin status for user {}: {}", userId, user.getIsAdmin());

            return ResponseEntity.ok(user.getIsAdmin());
        } catch (Exception e) {
            logger.error("Error checking admin status", e);
            throw new BadRequestException("Error checking admin status: " + e.getMessage());
        }
    }
}