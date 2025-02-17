package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.repository.UserRepository;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.model.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private UserRegistrationStatusService registrationStatusService;

    public AuthResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        Users user = new Users();
        user.setUserId(idGeneratorService.generateUserId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegistrationStatus(Users.RegistrationStatus.PENDING);
        user.setIsAdmin(false);

        Users savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser);
        registrationStatusService.createStatus(user.getUserId());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), true);
    }

    public AuthResponse login(LoginRequest request) {
        Optional<Users> userOptional = userRepository.findByEmail(request.getIdentifier());

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUsername(request.getIdentifier());
        }

        if (userOptional.isEmpty()) {
            throw new BadRequestException("Invalid email/username or password");
        }

        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
            throw new BadRequestException("Email/username cannot be empty");
        }

        Users user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email/username or password");
        }

        String token = tokenProvider.generateToken(user);

        boolean isFirstLogin = registrationStatusService.isFirstLogin(user.getUserId());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), isFirstLogin);
    }

    public Users getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));
    }
}
