package com.example.Bibliotech_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthResponse {

    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotBlank(message = "Tên người dùng không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private boolean isFirstLogin;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String email, boolean isFirstLogin) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.isFirstLogin = isFirstLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }
}
