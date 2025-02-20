package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRegistrationStatusService registrationStatusService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
}
